from __future__ import absolute_import
import sys
# from abc import ABC, abstractmethod
from collections import deque
# from math import float('inf')
from pprint import pprint
from Queue import Queue, PriorityQueue
# from queue import PriorityQueue
from time import time, sleep
# from typing import Dict, Iterable, Type, Tuple, Set, Generic, TypeVar, List, Union, Callable

from problem import StripsOperator, Strips

# Utilities
# Item = TypeVar(u'Item')


class CustomPriorityQueue():
    def __init__(self):
        self.queue = PriorityQueue()

    def put(self, key, node):
        self.queue.put((key, node))

    def get(self):
        _, item = self.queue.get()
        return item

    def empty(self):
        return self.queue.empty()


# class FastQueue():
#     def __init__(self, max_key):
#         self.items = 0
#         self.lowest_nonempty = 0
#         self.max_key = max_key
#         self.queues = [deque() for _ in xrange(max_key + 1)]
#
#     def put(self, key, node):
#         if key > self.max_key:
#             raise Exception(u"key to big")
#         self.queues[key].append(node)
#         self.items += 1
#
#     def get(self):
#         while self.lowest_nonempty <= self.max_key:
#             queue = self.queues[self.lowest_nonempty]
#             if len(queue) == 0:
#                 self.lowest_nonempty += 1
#             else:
#                 return queue.pop()
#         raise Exception(u"Queue empty")
#
#     def empty(self):
#         return self.items == 0


# class IntegerPriorityQueue():
#     def __init__(self):
#         self.queues = {}
#
#     def get_queue(self, key):
#         if key not in self.queues:
#             self.queues[key] = Queue()
#         return self.queues[key]
#
#     def put(self, key, node):
#         self.get_queue(key).put(node)
#
#     def get(self):
#         for key in sorted(self.queues):
#             if not self.queues[key].empty():
#                 return self.queues[key].get()
#
#     def empty(self):
#         for _, Queue in self.queues.items():
#             if not Queue.empty():
#                 return False
#         return True
#
#     def __contains__(self, item):
#         for _, Queue in self.queues.items():
#             if item in Queue:
#                 return True
#         return False
#

# Problem and State definition
# Fact = TypeVar(u"Fact", bound=int)


class State(object):
    def __init__(self, facts, g=float('inf'), h=float('inf')):
        self.facts = facts
        self.g = g
        self.h = h
        self.previous_state = None
        self.previous_operator = None

        self._fact_hash = hash(sum(i for i in self.facts))

    def set_previous(self, state, operator):
        self.previous_operator = operator
        self.previous_state = state
        self.g = state.g + operator.cost

    def __contains__(self, state):
        return state.facts.issubset(self.facts)

    def __eq__(self, other):
        return self.facts == other.facts

    def __hash__(self):
        return self._fact_hash

    def __str__(self):
        return u"<State> g: %0.2f, h: %0.2f facts: %s" % (self.g, self.h, unicode(self.facts))

    @property
    def f(self):
        if self.h is None:
            raise ValueError(u"%s: h is None" % unicode(self))
        return self.g + self.h

    def __lt__(self, other):
        return self.h < other.h


class OperatorBag(object):
    def __init__(self, facts, operators):
        self._by_pre = [[] for _ in xrange(len(facts))]
        for operator in operators:
            for fact in operator.pre:
                self._by_pre[fact].append(operator)

        self._by_add = [[] for _ in xrange(len(facts))]
        for operator in operators:
            for fact in operator.add_eff:
                self._by_add[fact].append(operator)

    def get_by_pre(self, fact):
        return self._by_pre[fact]

    def get_by_add(self, fact):
        return self._by_add[fact]


class Problem(object):
    def __init__(self, facts, operators, goal_facts,
                 init_facts):
        self._init = frozenset(init_facts)
        self._goal = frozenset(goal_facts)
        self.operators = tuple(operators)
        self.facts = frozenset(facts)
        self.operator_bag = OperatorBag(facts, operators)

    @classmethod
    def from_strips(cls, strips):
        facts = set(xrange(len(strips.facts)))
        operators = strips.operators
        goal_facts = strips.goal
        init_facts = strips.init
        return cls(facts, operators, goal_facts, init_facts)

    @property
    def start_state(self):
        return State(set(self._init), g=0)

    @property
    def goal_state(self):
        return State(set(self._goal), h=0)

    def __str__(self):
        return u"Facts: " + unicode(self.facts) + u"\n" + \
               u"Init facts: " + unicode(self._init) + u"\n" + \
               u"Goal facts: " + unicode(self._goal)


class IGProblem(Problem):
    def __init__(self, facts, operators, goal_facts,
                 init_facts, goal_fact, init_fact):
        super(IGProblem, self).__init__(facts, operators, goal_facts, init_facts)
        self.goal_fact = goal_fact
        self.init_fact = init_fact


# Plan

class Plan(object):
    def __init__(self, start_state, final_state):
        self.start_state = start_state
        self.final_state = final_state
        self.plan = self._extract_solution(start_state, final_state)

    @staticmethod
    def _extract_solution(start_state, final_state):
        path = [final_state]

        state = final_state
        while state != start_state:
            path.append(state.previous_operator)
            path.append(state.previous_state)
            state = state.previous_state

        path.reverse()
        return path

    @property
    def cost(self):
        return self.final_state.g

    def __str__(self):
        s = u""
        for obj in self.plan:
            if isinstance(obj, State):
                s += unicode(obj)
            elif isinstance(obj, StripsOperator):
                s += u" -> " + unicode(obj.name) + u"\n"
        return s

    def to_plan_file(self):
        operators = []
        for obj in self.plan:
            if isinstance(obj, StripsOperator):
                operators.append(obj.name)
        return u"\n".join(operators)


class PlanNotFound(Plan):
    def __init(self, start_state, final_state):
        pass

    def __str__(self):
        return u"Plan not found."


# Problem specific classes

class StateExpander(object):
    # todo: if needed, build better representation of operator bag
    def __init__(self, problem):
        self.operators = self._init_operator_tree(problem.operators)

    def expand(self, state):
        states = []
        for operator in self.operators:
            if operator.pre.issubset(state.facts):
                facts = (state.facts - operator.del_eff) | operator.add_eff
                states.append((operator, State(facts)))
        return states

    @staticmethod
    def _init_operator_tree(operators):
        return operators


class Heuristic():
    def __init__(self, problem):
        self.problem = problem

    def estimate(self, state, goal_state):
        pass


class Hmax(Heuristic):
    def estimate(self, state, goal_state):
        delta = self.initialize_estimate(state)

        remaining_facts = set(self.problem.facts)
        not_visited_goal_facts = set(goal_state.facts)
        while len(not_visited_goal_facts) != 0:
            _, c = self.optimize_delta(remaining_facts, delta)

            remaining_facts.remove(c)
            not_visited_goal_facts -= set([c])

            for operator in self.problem.operator_bag.get_by_pre(c):
                operator.U -= 1

                if operator.U == 0:
                    for fact in operator.add_eff:
                        delta[fact] = min(delta[fact], operator.cost + delta[c])

        h_max, _ = self.optimize_delta(goal_state.facts, delta, minimization=False)
        return h_max

    @staticmethod
    def optimize_delta(facts, delta, minimization=True):
        opt_val = float('inf') if minimization else -float('inf')
        opt_arg = None
        for f in facts:
            if (minimization and delta[f] <= opt_val) or (not minimization and delta[f] >= opt_val):
                opt_val = delta[f]
                opt_arg = f

        return opt_val, opt_arg

    def initialize_estimate(self, state):
        delta = [float('inf')] * len(self.problem.facts)
        for fact in state.facts:
            delta[fact] = 0

        for operator in self.problem.operators:
            operator.U = len(operator.pre)

        return delta


class HmaxLm(Hmax):
    def estimate(self, state, goal_state):
        delta = self.initialize_estimate(state)
        supporter = dict()

        remaining_facts = set(self.problem.facts)
        # visited_goal_fact = False
        while len(remaining_facts) > 0:
            _, c = self.optimize_delta(remaining_facts, delta)

            remaining_facts.remove(c)
            # visited_goal_fact = self.problem.goal_fact == c

            for operator in self.problem.operator_bag.get_by_pre(c):
                operator.U -= 1
                if operator.U == 0:
                    supporter[operator.name] = c
                    for fact in operator.add_eff:
                        if operator.cost + delta[c] < delta[fact]:
                            delta[fact] = operator.cost + delta[c]

        h_max = delta[self.problem.goal_fact]
        # print("hmax: %s" % str(h_max))
        return h_max, supporter


class LmCut(Heuristic):
    def estimate(self, state, goal_state):
        # print("--- LmCut for state: %s" % str(state))

        lm_cut_value = 0
        problem = self._construct_problem(state, goal_state)
        h_max = HmaxLm(problem)

        h, supporter = h_max.estimate(problem.start_state, problem.goal_state)
        while h != 0:
            if h == float('inf'):
                # print("lm = float('inf')")
                return float('inf')

            landmarks = self._get_landmarks(problem, supporter)
            min_landmark_cost = min(landmark.cost for landmark in landmarks)
            lm_cut_value += min_landmark_cost

            for landmark in landmarks:
                landmark.cost -= min_landmark_cost

            h, supporter = h_max.estimate(problem.start_state, problem.goal_state)

        # print("lm-cut: %d" % lm_cut_value)
        return lm_cut_value

    @staticmethod
    def _get_landmarks(problem, supporter):
        # backward BFS walk
        landmark_candidates = set()
        queue = deque()
        queue.append(problem.goal_fact)
        visited_facts = set()

        while len(queue) > 0:
            fact = queue.pop()
            visited_facts.add(fact)

            for operator in problem.operator_bag.get_by_add(fact):
                # if operator.name not in supporter:
                #     continue

                if operator.cost == 0 and supporter[operator.name] not in visited_facts:
                    queue.append(supporter[operator.name])
                elif operator.cost > 0:
                    landmark_candidates.add(operator.name)

        # forward BFS walk
        landmarks = set()
        queue = deque()
        queue.append(problem.init_fact)
        visited_facts = set()

        while len(queue) > 0:
            fact = queue.pop()
            visited_facts.add(fact)

            for operator in problem.operator_bag.get_by_pre(fact):
                if operator.name in supporter and fact == supporter[operator.name]:
                    if operator.name in landmark_candidates:
                        landmarks.add(operator)
                        landmark_candidates.remove(operator.name)
                    else:
                        for next_fact in operator.add_eff:
                            if next_fact not in visited_facts:
                                queue.append(next_fact)

        return landmarks

    def _construct_problem(self, state, goal_state):
        init_fact = len(self.problem.facts)
        goal_fact = init_fact + 1
        facts = self.problem.facts | set([init_fact, goal_fact])

        init_o = StripsOperator(u"init_operator", [init_fact], state.facts, [], 0)
        goal_o = StripsOperator(u"goal_operator", goal_state.facts, [goal_fact], [], 0)

        new_operators = []
        for operator in self.problem.operators:
            new_operator = StripsOperator(u"", operator.pre, operator.add_eff, operator.del_eff,
                                          operator.cost)
            new_operator.name = operator.name  # dirty trick
            new_operators.append(new_operator)
        new_operators += [init_o, goal_o]

        goal_facts = set([goal_fact])
        init_facts = set([init_fact])
        return IGProblem(facts, new_operators, goal_facts, init_facts, goal_fact, init_fact)


class LmCutCashed(LmCut):
    def estimate(self, state, goal_state):
        if state not in self.cached_h:
            lm_cut_value = LmCut.estimate(self, state, goal_state)
            self.cached_h[state] = lm_cut_value
        return self.cached_h[state]

    def __init__(self, problem):
        LmCut.__init__(self, problem)
        self.cached_h = {}


class UnfulfilledFacts(Heuristic):
    def estimate(self, state, goal_state):
        return len(goal_state.facts - state.facts)


class DummyHeuristic(Heuristic):
    def estimate(self, state, goal_state):
        return 1.0


# A* implementation

class Solver(object):
    def __init__(self,
                 priority_queue,
                 state_expander,
                 heuristics,
                 problem_representation):
        self.problem_representation = problem_representation
        self.heuristics = heuristics
        self.state_expander = state_expander
        self.open_states = priority_queue

        self._plan = None

        self.init_state_h = None

        self.num_opened_states = 0
        self.num_added_to_queue_states = 0

    @property
    def plan(self):
        if self._plan is None:
            self._plan = self._compute_plan()
        return self._plan

    def _compute_plan(self):
        goal_state = self.problem_representation.goal_state
        start_state = self.problem_representation.start_state
        start_state.h = self.heuristics.estimate(start_state, goal_state)
        self.init_state_h = start_state.h

        closed_states = set()
        # distance = {}

        self.open_states.put(start_state.g, start_state)

        i = 0
        while not self.open_states.empty():
            i += 1
            # if i % 100 == 0:
            # print("A* opened node number: %d" % i)

            self.num_opened_states += 1
            current_state = self.open_states.get()

            # if current_state not in closed_states: # or current_state.g < distance[current_state]:
            closed_states.add(current_state)
            # distance[current_state] = current_state.g

            if goal_state in current_state:
                return Plan(start_state, current_state)

            for operator, neighbour in self.state_expander.expand(current_state):
                neighbour.h = self.heuristics.estimate(neighbour, goal_state)
                neighbour.set_previous(current_state, operator)

                if neighbour.h < float('inf') and neighbour not in closed_states:
                    self.num_added_to_queue_states += 1
                    self.open_states.put(neighbour.f, neighbour)
                    # else:
                    #     raise Exception("h = float('inf')")

        return PlanNotFound(start_state, goal_state)


# Time

class Timer(object):
    def __init__(self, name):
        self.name = name
        self.time_start = time()
        self._checkpoints = [(name, self.time_start)]

    def _print_last(self):
        last_idx = len(self._checkpoints) - 1
        name, time = self._checkpoints[last_idx]
        _, previous_time = self._checkpoints[last_idx - 1]
        print u"<Timer> %s: %0.5f s" % (name, time - previous_time)

    def checkpoint(self, name, verbose=True):
        self._checkpoints.append((name, time()))
        if verbose:
            self._print_last()

    def finish(self):
        print u"<Timer> %s: %0.2f s" % (u"Finish", time() - self.time_start)


# Check Utils

def correctness_check(problem_representation, plan):
    if isinstance(plan, PlanNotFound):
        return False
    return problem_representation.goal_state.facts.issubset(plan.final_state.facts)


def efficiency_check(plan_builder):
    return u"Popped from queue states: " + unicode(plan_builder.num_opened_states) + u"\n" + \
           u"Added to queue: " + unicode(plan_builder.num_added_to_queue_states) + u"\n"


def main(fn_strips):
    strips = Strips(fn_strips)

    # timer = Timer("Start")

    problem_representation = Problem.from_strips(strips)
    heuristics = LmCutCashed(problem_representation)
    state_expander = StateExpander(problem_representation)

    # timer.checkpoint("Operator Tree built")

    plan_builder = Solver(CustomPriorityQueue(), state_expander, heuristics, problem_representation)
    plan = plan_builder.plan

    # timer.checkpoint("Plan built")

    print u";; Cost: %d" % plan.cost
    print u";; Init: %d" % plan_builder.init_state_h
    print
    print plan.to_plan_file()

    # print()
    # print("Correctness of the plan: " + str(correctness_check(problem_representation, plan)))
    # print(efficiency_check(plan_builder))
    #
    # timer.finish()


if __name__ == u'__main__':
    if len(sys.argv) != 3:
        print u'Usage: {0} problem.strips problem.fdr'.format(sys.argv[0])
        sys.exit(-1)

    main(sys.argv[1])