package org.endeavourhealth.enterprise.engine.compiler;

import org.apache.commons.collections4.CollectionUtils;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentHelper;
import org.endeavourhealth.enterprise.core.querydocument.models.Query;
import org.endeavourhealth.enterprise.core.querydocument.models.Rule;
import org.endeavourhealth.enterprise.core.querydocument.models.Test;
import org.endeavourhealth.enterprise.engine.UnableToCompileExpection;
import org.endeavourhealth.enterprise.engine.compiled.*;
import org.endeavourhealth.enterprise.enginecore.InvalidQueryDocumentException;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class QueryCompiler {
    private final NodeCompiler nodeCompiler = new NodeCompiler();
    private final TestCompiler testCompiler = new TestCompiler();

    public CompiledQuery compile(CompilerContext compilerContext, Query query) throws InvalidQueryDocumentException, UnableToCompileExpection {

        HashMap<Rule, ICompiledRule> compiledRules = compileRules(query.getRule(), compilerContext);

        nodeCompiler.compile(query);
        nodeCompiler.getRuleNodeRelationships();
        NodeTraversal nodeTraversal = nodeCompiler.getNodeTraversal();

        HashMap<Node, ICompiledRule> nodeToCompiledRule = createNodeToCompiledRule(compiledRules, nodeCompiler.getRuleNodeRelationships());

        UUID parentQueryUuid = QueryDocumentHelper.parseOptionalUuid(query.getParentQueryUuid());

        CompiledQuery compiledQuery = new CompiledQuery(
                nodeTraversal,
                nodeToCompiledRule,
                parentQueryUuid);

        nodeTraversal.initialise(compiledQuery);

        return compiledQuery;
    }

    private HashMap<Node, ICompiledRule> createNodeToCompiledRule(HashMap<Rule, ICompiledRule> compiledRules, RuleNodeRelationships ruleNodeRelationships) {
        HashMap<Node, ICompiledRule> map = new HashMap<>();

        for (Rule rule: compiledRules.keySet()) {
            Node node = ruleNodeRelationships.getNodeFromRule(rule);
            map.put(node, compiledRules.get(rule));
        }

        return map;
    }

    private HashMap<Rule, ICompiledRule> compileRules(List<Rule> rules, CompilerContext compilerContext) throws InvalidQueryDocumentException, UnableToCompileExpection {

        if (CollectionUtils.isEmpty(rules))
            throw new InvalidQueryDocumentException("No rules present in Query");

        HashMap<Rule, ICompiledRule> ruleMap = new HashMap<>();

        for (Rule rule: rules) {

            try {
                ICompiledRule compiledRule;

                if (rule.getTest() != null)
                    compiledRule = compileRuleAsTest(rule.getTest(), compilerContext);
                else if (rule.getTestLibraryItemUUID() != null)
                    compiledRule = compileRuleAsTestLibraryItem(rule.getTestLibraryItemUUID());
                else if (rule.getQueryLibraryItemUUID() != null)
                    compiledRule = compileRuleAsQuery(rule.getQueryLibraryItemUUID());
                else
                    throw new UnableToCompileExpection("Rule type not supported");

                ruleMap.put(rule, compiledRule);
            } catch (Exception e) {
                throw new UnableToCompileExpection("Error in RuleID: " + rule.getId(), e);
            }
        }

        return ruleMap;
    }

    private ICompiledRule compileRuleAsQuery(String queryLibraryItemUUID) {
        return null;
    }

    private ICompiledRule compileRuleAsTestLibraryItem(String testLibraryItemUUID) {
        UUID uuid = QueryDocumentHelper.parseMandatoryUuid(testLibraryItemUUID);
        return new CompiledRuleOfTypeTest(uuid);
    }

    private ICompiledRule compileRuleAsTest(Test test, CompilerContext compilerContext) throws Exception {
        ICompiledTest compiledTest = testCompiler.compile(test, compilerContext);
        UUID uuid = UUID.randomUUID();
        compilerContext.getCompiledLibrary().addInlineTest(uuid, compiledTest);

        return new CompiledRuleOfTypeTest(uuid);
    }
}
