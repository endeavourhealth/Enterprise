package org.endeavourhealth.enterprise.engine.compiler

import org.endeavourhealth.enterprise.enginecore.InvalidQueryDocumentException
import org.junit.Test

import static org.mockito.Mockito.*;

import org.endeavourhealth.enterprise.core.querydocument.models.Query
import org.endeavourhealth.enterprise.core.querydocument.models.Rule
import org.endeavourhealth.enterprise.core.querydocument.models.RuleAction
import org.endeavourhealth.enterprise.core.querydocument.models.RuleActionOperator
import org.endeavourhealth.enterprise.engine.compiled.INodeExecutor
import org.endeavourhealth.enterprise.engine.compiled.NodeTraversal

class NodeCompilerTest {

    @Test
    void testCompile_singleRuleTruePIFN_pass() {

        new NodeBuilder()
                .addPassingRule(1, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .registerStartingNode(1)
                .assertPass();
    }

    @Test
    void testCompile_singleRuleFalsePIFN_fail() {

        new NodeBuilder()
                .addFailingRule(1, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .registerStartingNode(1)
                .assertFail();
    }

    @Test
    void testCompile_singleRuleTruePNFI_fail() {

        new NodeBuilder()
                .addPassingRule(1, RuleActionOperator.NO_ACTION, RuleActionOperator.INCLUDE)
                .registerStartingNode(1)
                .assertFail();
    }

    @Test
    void testCompile_singleRuleFalsePNFI_pass() {

        new NodeBuilder()
                .addFailingRule(1, RuleActionOperator.NO_ACTION, RuleActionOperator.INCLUDE)
                .registerStartingNode(1)
                .assertPass();
    }

    @Test
    void testCompile_multiStartRuleFirstPassSecondFail_pass() {

        new NodeBuilder()
                .addPassingRule(1, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .addFailingRule(2, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .registerStartingNode(1)
                .registerStartingNode(2)
                .assertPass();
    }

    @Test
    void testCompile_multiStartRuleFirstFailSecondPass_pass() {

        new NodeBuilder()
                .addFailingRule(1, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .addPassingRule(2, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .registerStartingNode(1)
                .registerStartingNode(2)
                .assertPass();
    }

    @Test
    void testCompile_multiStartRuleBothFail_fail() {

        new NodeBuilder()
                .addFailingRule(1, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .addFailingRule(2, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .registerStartingNode(1)
                .registerStartingNode(2)
                .assertFail();
    }

    @Test
    void testCompile_simpleChain1_pass() {

        new NodeBuilder()
                .addPassingRule(1, [2], RuleActionOperator.NO_ACTION)
                .addPassingRule(2, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .registerStartingNode(1)
                .assertPass();
    }

    @Test
    void testCompile_simpleChain2_fail() {

        new NodeBuilder()
                .addPassingRule(1, [2], RuleActionOperator.NO_ACTION)
                .addFailingRule(2, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .registerStartingNode(1)
                .assertFail();
    }

    @Test
    void testCompile_orFirst_pass() {

        new NodeBuilder()
                .addPassingRule(1, [2, 3], RuleActionOperator.NO_ACTION)
                .addPassingRule(2, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .addFailingRule(3, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .registerStartingNode(1)
                .assertPass();
    }

    @Test
    void testCompile_orSecond_pass() {

        new NodeBuilder()
                .addPassingRule(1, [2, 3], RuleActionOperator.NO_ACTION)
                .addFailingRule(2, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .addPassingRule(3, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .registerStartingNode(1)
                .assertPass();
    }

    @Test
    void testCompile_orNeither_fail() {

        new NodeBuilder()
                .addPassingRule(1, [2, 3], RuleActionOperator.NO_ACTION)
                .addFailingRule(2, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .addFailingRule(3, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .registerStartingNode(1)
                .assertFail();
    }

    @Test
    void testCompile_negateOrFirst_pass() {

        new NodeBuilder()
                .addFailingRule(1, RuleActionOperator.NO_ACTION, [2, 3])
                .addPassingRule(2, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .addFailingRule(3, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .registerStartingNode(1)
                .assertPass();
    }

    @Test
    void testCompile_negateOrSecond_pass() {

        new NodeBuilder()
                .addFailingRule(1, RuleActionOperator.NO_ACTION, [2, 3])
                .addFailingRule(2, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .addPassingRule(3, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .registerStartingNode(1)
                .assertPass();
    }

    @Test
    void testCompile_negateOrNeither_fail() {

        new NodeBuilder()
                .addFailingRule(1, RuleActionOperator.NO_ACTION, [2, 3])
                .addFailingRule(2, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .addFailingRule(3, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .registerStartingNode(1)
                .assertFail();
    }

    @Test
    void testCompile_multiStartingNodesFirst_pass() {

        new NodeBuilder()
                .addPassingRule(1, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .addFailingRule(2, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .registerStartingNode(1)
                .registerStartingNode(2)
                .assertPass();
    }

    @Test
    void testCompile_multiStartingNodesSecond_pass() {

        new NodeBuilder()
                .addFailingRule(1, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .addPassingRule(2, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .registerStartingNode(1)
                .registerStartingNode(2)
                .assertPass();
    }

    @Test
    void testCompile_multiStartingNodesNeither_fail() {

        new NodeBuilder()
                .addFailingRule(1, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .addFailingRule(2, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .registerStartingNode(1)
                .registerStartingNode(2)
                .assertFail();
    }

    @Test
    void testCompile_outcomeBothNoAction_exception() {

        new NodeBuilder()
                .addPassingRule(1, RuleActionOperator.NO_ACTION, RuleActionOperator.NO_ACTION)
                .registerStartingNode(1)
                .shouldFailDueToQueryDocument();
    }

    @Test
    void testCompile_outcomeBothInclude_exception() {

        new NodeBuilder()
                .addPassingRule(1, RuleActionOperator.INCLUDE, RuleActionOperator.INCLUDE)
                .registerStartingNode(1)
                .shouldFailDueToQueryDocument();
    }

    @Test
    void testCompile_outcomeBothGoToSameNode_exception() {

        new NodeBuilder()
                .addPassingRule(1, [2], [3, 2])
                .addPassingRule(2, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .addPassingRule(3, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .registerStartingNode(1)
                .shouldFailDueToQueryDocument();
    }

    @Test
    void testCompile_noStartingNodes_exception() {

        new NodeBuilder()
                .addPassingRule(1, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .shouldFailDueToQueryDocument();
    }

    @Test
    void testCompile_nodeNotCalled_exception() {

        new NodeBuilder()
                .addPassingRule(1, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .addPassingRule(2, RuleActionOperator.INCLUDE, RuleActionOperator.NO_ACTION)
                .registerStartingNode(1)
                .shouldFailDueToQueryDocument();
    }

    @Test
    void testCompile_cycle1_exception() {

        new NodeBuilder()
                .addPassingRule(1, [1], RuleActionOperator.NO_ACTION)
                .registerStartingNode(1)
                .shouldFailDueToQueryDocument();
    }

    @Test
    void testCompile_cycle2_exception() {

        new NodeBuilder()
                .addPassingRule(1, RuleActionOperator.NO_ACTION, [1])
                .registerStartingNode(1)
                .shouldFailDueToQueryDocument();
    }

    @Test
    void testCompile_cycle3_exception() {

        new NodeBuilder()
                .addPassingRule(1, [2], RuleActionOperator.NO_ACTION)
                .addPassingRule(2, RuleActionOperator.NO_ACTION, [1])
                .registerStartingNode(1)
                .shouldFailDueToQueryDocument();
    }

    @Test
    void testCompile_cycle4_exception() {

        new NodeBuilder()
                .addPassingRule(1, [2], RuleActionOperator.NO_ACTION)
                .addPassingRule(2, RuleActionOperator.NO_ACTION, [3])
                .addPassingRule(3, [1], RuleActionOperator.INCLUDE)
                .registerStartingNode(1)
                .shouldFailDueToQueryDocument();
    }

    static class NodeBuilder {

        private final Query query = new Query();
        private final INodeExecutor mockedNodeExecutor = mock(INodeExecutor);
        private final List<Rule> failingRules = new ArrayList<>();
        private final List<Rule> passingRules = new ArrayList<>();

        public NodeBuilder addPassingRule(int id, RuleActionOperator trueOperator, RuleActionOperator falseOperator) {
            Rule rule = buildRule(id, trueOperator, falseOperator)
            passingRules.add(rule);
            return this;
        }

        public NodeBuilder addFailingRule(int id, RuleActionOperator trueOperator, RuleActionOperator falseOperator) {
            Rule rule = buildRule(id, trueOperator, falseOperator)
            failingRules.add(rule);
            return this;
        }

        public NodeBuilder addPassingRule(int id, List<Integer> trueRuleIds, RuleActionOperator falseOperator) {
            Rule rule = buildRule(id, RuleActionOperator.GOTO_RULES, falseOperator)
            rule.onPass.ruleId.addAll(trueRuleIds);
            passingRules.add(rule);
            return this;
        }

        public NodeBuilder addPassingRule(int id, List<Integer> trueRuleIds, List<Integer> faleRuleIds) {
            Rule rule = buildRule(id, RuleActionOperator.GOTO_RULES, RuleActionOperator.GOTO_RULES)
            rule.onPass.ruleId.addAll(trueRuleIds);
            rule.onFail.ruleId.addAll(faleRuleIds);
            passingRules.add(rule);
            return this;
        }

        public NodeBuilder addPassingRule(int id, RuleActionOperator trueOperator, List<Integer> falseRuleIds) {
            Rule rule = buildRule(id, trueOperator, RuleActionOperator.GOTO_RULES)
            rule.onFail.ruleId.addAll(falseRuleIds);
            failingRules.add(rule);
            return this;
        }

        public NodeBuilder addFailingRule(int id, List<Integer> trueRuleIds, RuleActionOperator falseOperator) {
            Rule rule = buildRule(id, RuleActionOperator.GOTO_RULES, falseOperator)
            rule.onPass.ruleId.addAll(trueRuleIds);
            failingRules.add(rule);
            return this;
        }

        public NodeBuilder addFailingRule(int id, RuleActionOperator trueOperator, List<Integer> falseRuleIds) {
            Rule rule = buildRule(id, trueOperator, RuleActionOperator.GOTO_RULES)
            rule.onFail.ruleId.addAll(falseRuleIds);
            failingRules.add(rule);
            return this;
        }

        private Rule buildRule(int id, RuleActionOperator trueOperator, RuleActionOperator falseOperator) {
            Rule rule = new Rule();
            rule.id = id;
            rule.onPass = new RuleAction(action: trueOperator);
            rule.onFail = new RuleAction(action: falseOperator);
            query.rule.add(rule);
            rule
        }

        public NodeBuilder registerStartingNode(int ruleId) {
            if (query.startingRules == null)
                query.startingRules = new Query.StartingRules();

            query.startingRules.ruleId.add(ruleId);

            return this;
        }

        public void assertFail() {
            NodeTraversal traversal = createTraversal();
            assert !traversal.execute();
        }

        public void assertPass() {
            NodeTraversal traversal = createTraversal();
            assert traversal.execute();
        }

        public void shouldFailDueToQueryDocument() {

            try {
                createTraversal();
                fail( "Expected InvalidQueryDocumentException exception but no exception thrown" );
            } catch (InvalidQueryDocumentException e) {
            }
        }

        public NodeTraversal createTraversal() {

            NodeCompiler compiler = new NodeCompiler();
            compiler.compile(query);

            NodeTraversal traversal = compiler.nodeTraversal;

            for (Rule rule: passingRules)
                when(mockedNodeExecutor.execute(compiler.ruleNodeRelationships.getNodeFromRule(rule))).thenReturn(true);

            for (Rule rule: failingRules)
                when(mockedNodeExecutor.execute(compiler.ruleNodeRelationships.getNodeFromRule(rule))).thenReturn(false);

            traversal.initialise(mockedNodeExecutor);
            return traversal;
        }
    }
}
