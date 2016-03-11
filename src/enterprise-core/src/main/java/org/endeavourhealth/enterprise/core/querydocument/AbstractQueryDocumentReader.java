package org.endeavourhealth.enterprise.core.querydocument;

import org.endeavourhealth.enterprise.core.querydocument.models.*;

import java.util.List;
import java.util.Stack;

/**
 * Created by Drew on 11/03/2016.
 */
public abstract class AbstractQueryDocumentReader {

    private QueryDocument doc = null;
    private Stack<Object> stack = new Stack<Object>();

    public AbstractQueryDocumentReader(QueryDocument doc) {
        this.doc = doc;
    }

    protected final Stack<Object> getStack() {
        return stack;
    }

    protected final <T extends Object> T getLast(Class<T> type) {

        for (int i=stack.size(); i>=0; i--) {
            Object obj = stack.get(i);
            if (obj.getClass().equals(type)) {
                return type.cast(obj);
            }
        }
        return null;
    }



    public final void process() {

        try {
            stack.push(doc);        

            List<Folder> folders = doc.getFolder();
            if (folders != null && !folders.isEmpty()) {
                for (Folder folder: folders) {
                    processFolder(folder);
                }
            }
    
            List<LibraryItem> libraryItems = doc.getLibraryItem();
            if (libraryItems != null && !libraryItems.isEmpty()) {
                for (LibraryItem libraryItem: libraryItems) {
                    processLibraryItem(libraryItem);
                }
            }
    
            List<Report> reports = doc.getReport();
            if (reports != null && !reports.isEmpty()) {
                for (Report report: reports) {
                    processReport(report);
                }
            }

        } finally {
            stack.pop();
        }
    }

    protected void processFolder(Folder folder) {
        //no child complex types to recurse to
    }


    protected void processLibraryItem(LibraryItem libraryItem) {
        try {
            stack.push(libraryItem);

            if (libraryItem.getQuery() != null) {
                processQuery(libraryItem.getQuery());
            }

            if (libraryItem.getDataSource() != null) {
                processDataSource(libraryItem.getDataSource());
            }

            if (libraryItem.getTest() != null) {
                processTest(libraryItem.getTest());
            }

            if (libraryItem.getCodeSet() != null) {
                processCodeSet(libraryItem.getCodeSet());
            }

            if (libraryItem.getListOutput() != null) {
                processListOutput(libraryItem.getListOutput());
            }

        } finally {
            stack.pop();
        }
    }

    protected void processCodeSet(CodeSet codeSet) {
        try {
            stack.push(codeSet);

            if (codeSet.getCodingSystem() != null) {
                processCodingSystem(codeSet.getCodingSystem());
            }

            List<CodeSetValue> values = codeSet.getCodeSetValue();
            if (values != null && !values.isEmpty()) {
                for (CodeSetValue value: values) {
                    processCodeSetValue(value);
                }
            }

        } finally {
            stack.pop();
        }
    }

    protected void processCodingSystem(CodingSystem codingSystem) {
        //no child complex types to recurse to
    }

    protected void processCodeSetValue(CodeSetValue codeSetValue) {
        try {
            stack.push(codeSetValue);

            List<CodeSetValue> exclusions = codeSetValue.getExclusion();
            if (exclusions != null && !exclusions.isEmpty()) {
                for (CodeSetValue exclusion: exclusions) {
                    processCodeSetValue(exclusion);
                }
            }

        } finally {
            stack.pop();
        }
    }

    protected void processListOutput(ListOutput listOutput) {
        try {
            stack.push(listOutput);

            List<ListGroup> groups = listOutput.getGroup();
            if (groups != null && !groups.isEmpty()) {
                for (ListGroup group: groups) {
                    processListGroup(group);
                }
            }

        } finally {
            stack.pop();
        }
    }

    protected void processListGroup(ListGroup listGroup) {
        try {
            stack.push(listGroup);

            if (listGroup.getSummary() != null) {
                processListGroupSummary(listGroup.getSummary());
            }

            if (listGroup.getField() != null) {
                processListGroupField(listGroup.getField());
            }

        } finally {
            stack.pop();
        }
    }

    protected void processListGroupSummary(ListGroup.Summary listGroupSummary) {
        //no child complex types to recurse to
    }

    protected void processListGroupField(ListGroup.Field listGroupField) {
        try {
            stack.push(listGroupField);

            List<FieldOutput> fieldOutputs = listGroupField.getFieldOutput();
            if (fieldOutputs != null && !fieldOutputs.isEmpty()) {
                for (FieldOutput fieldOutput: fieldOutputs) {
                    processFieldOutput(fieldOutput);
                }
            }

        } finally {
            stack.pop();
        }
    }

    protected void processFieldOutput(FieldOutput fieldOutput) {
        //no child complex types to recurse to
    }


    protected void processQuery(Query query) {
        try {
            stack.push(query);

            if (query.getStartingRules() != null) {
                processQueryStartingRules(query.getStartingRules());
            }

            List<Rule> rules = query.getRule();
            if (rules != null && !rules.isEmpty()) {
                for (Rule rule: rules) {
                    processRule(rule);
                }
            }

        } finally {
            stack.pop();
        }
    }

    protected void processQueryStartingRules(Query.StartingRules startingRules) {
        //no child complex types to recurse to
    }

    protected void processRule(Rule rule) {

        try {
            stack.push(rule);

            if (rule.getTest() != null) {
                processTest(rule.getTest());
            }

            if (rule.getOnPass() != null) {
                processRuleAction(rule.getOnPass(), true);
            }

            if (rule.getOnFail() != null) {
                processRuleAction(rule.getOnFail(), false);
            }

            if (rule.getLayout() != null) {
                processLayoutType(rule.getLayout());
            }

        } finally {
            stack.pop();
        }
    }

    protected void processTest(Test test) {
        try {
            stack.push(test);

            if (test.getDataSource() != null) {
                processDataSource(test.getDataSource());
            }

            List<FieldTest> fieldTests = test.getFieldTest();
            if (fieldTests != null && !fieldTests.isEmpty()) {
                for (FieldTest fieldTest: fieldTests) {
                    processFieldTest(fieldTest);
                }
            }

            if (test.getCount() != null) {
                processComparison(test.getCount());
            }

            if (test.getLinkedTest() != null) {
                processLinkedTestType(test.getLinkedTest());
            }

        } finally {
            stack.pop();
        }
    }

    protected void processLinkedTestType(LinkedTestType linkedTestType) {
        try {
            stack.push(linkedTestType);

            if (linkedTestType.getDataSource() != null) {
                processDataSource(linkedTestType.getDataSource());
            }

            if (linkedTestType.getComparison() != null) {
                processComparison(linkedTestType.getComparison());
            }

        } finally {
            stack.pop();
        }
    }

    protected void processComparison(Comparison comparison) {
        try {
            stack.push(comparison);

            if (comparison.getValueFrom() != null) {
                processValueFrom(comparison.getValueFrom());
            }

            if (comparison.getValueTo() != null) {
                processValueTo(comparison.getValueTo());
            }

            if (comparison.getValueRange() != null) {
                processValueRange(comparison.getValueRange());
            }

            if (comparison.getValueEqualTo() != null) {
                processValue(comparison.getValueEqualTo());
            }

        } finally {
            stack.pop();
        }
    }

    protected void processDataSource(DataSource dataSource) {
        try {
            stack.push(dataSource);

            if (dataSource.getCalculation() != null) {
                processCalculationType(dataSource.getCalculation());
            }

            List<FieldTest> filters = dataSource.getFilter();
            if (filters != null && !filters.isEmpty()) {
                for (FieldTest filter: filters) {
                    processFieldTest(filter);
                }
            }

            if (dataSource.getRestriction() != null) {
                processRestriction(dataSource.getRestriction());
            }

        } finally {
            stack.pop();
        }
    }

    protected void processRestriction(Restriction restriction) {
        try {
            stack.push(restriction);

            if (restriction.getOrderDirection() != null) {
                processOrderDirection(restriction.getOrderDirection());
            }

        } finally {
            stack.pop();
        }
    }

    protected void processOrderDirection(OrderDirection orderDirection) {
        //no child complex types to recurse to
    }

    protected void processFieldTest(FieldTest fieldTest) {
        try {
            stack.push(fieldTest);

            if (fieldTest.getValueFrom() != null) {
                processValueFrom(fieldTest.getValueFrom());
            }

            if (fieldTest.getValueTo() != null) {
                processValueTo(fieldTest.getValueTo());
            }

            if (fieldTest.getValueRange() != null) {
                processValueRange(fieldTest.getValueRange());
            }

            if (fieldTest.getValueEqualTo() != null) {
                processValue(fieldTest.getValueEqualTo());
            }

            List<CodeSet> codeSets = fieldTest.getCodeSet();
            if (codeSets != null && !codeSets.isEmpty()) {
                for (CodeSet codeSet: codeSets) {
                    processCodeSet(codeSet);
                }
            }

        } finally {
            stack.pop();
        }
    }

    protected void processValueFrom(ValueFrom valueFrom) {
        try {
            stack.push(valueFrom);

            if (valueFrom.getOperator() != null) {
                processValueFromOperator(valueFrom.getOperator());
            }

        } finally {
            stack.pop();
        }

        //ValueFrom extends Value, so pass through to the fn to handle the generic instance
        processValue(valueFrom);
    }

    protected void processValueFromOperator(ValueFromOperator valueFromOperator) {
        //no child complex types to recurse to
    }

    protected void processParameterType(ParameterType parameterType) {
        //no child complex types to recurse to
    }

    protected void processValueAbsoluteUnit(ValueAbsoluteUnit valueAbsoluteUnit) {
        //no child complex types to recurse to
    }

    protected void processValueRelativeUnit(ValueRelativeUnit valueRelativeUnit) {
        //no child complex types to recurse to
    }

    protected void processValueTo(ValueTo valueTo) {
        try {
            stack.push(valueTo);

            if (valueTo.getOperator() != null) {
                processValueToOperator(valueTo.getOperator());
            }


        } finally {
            stack.pop();
        }

        //ValueTo extends Value, so pass through to the fn to handle the generic instance
        processValue(valueTo);
    }

    protected void processValueToOperator(ValueToOperator valueToOperator) {
        //no child complex types to recurse to
    }

    protected void processValueRange(ValueRange valueRange) {
        try {
            stack.push(valueRange);

            if (valueRange.getValueFrom() != null) {
                processValueFrom(valueRange.getValueFrom());
            }

            if (valueRange.getValueTo() != null) {
                processValueTo(valueRange.getValueTo());
            }

        } finally {
            stack.pop();
        }
    }

    protected void processValue(Value value) {
        try {
            stack.push(value);

            if (value.getParameter() != null) {
                processParameterType(value.getParameter());
            }

            if (value.getAbsoluteUnit() != null) {
                processValueAbsoluteUnit(value.getAbsoluteUnit());
            }

            if (value.getRelativeUnit() != null) {
                processValueRelativeUnit(value.getRelativeUnit());
            }

        } finally {
            stack.pop();
        }
    }

    protected void processCalculationType(CalculationType calculationType) {
        try {
            stack.push(calculationType);

            List<CalculationParameter> parameters = calculationType.getParameter();
            if (parameters != null && !parameters.isEmpty()) {
                for (CalculationParameter parameter: parameters) {
                    processCalculationParameter(parameter);
                }
            }

        } finally {
            stack.pop();
        }
    }

    protected void processCalculationParameter(CalculationParameter calculationParameter) {
        //no child complex types to recurse to
    }

    protected void processRuleAction(RuleAction ruleAction, boolean onPass) {
        try {
            stack.push(ruleAction);

            if (ruleAction.getAction() != null) {
                processRuleActionOperator(ruleAction.getAction());
            }

        } finally {
            stack.pop();
        }
    }

    protected void processRuleActionOperator(RuleActionOperator ruleActionOperator) {
        //no child complex types to recurse to
    }

    protected void processLayoutType(LayoutType layoutType) {
        //no child complex types to recurse to
    }

    protected void processReport(Report report) {
        try {
            stack.push(report);

            List<Report.Query> queries = report.getQuery();
            if (queries != null && !queries.isEmpty()) {
                for (Report.Query query: queries) {
                    processReportQuery(query);
                }
            }

            List<Report.ListOutput> listOutputs = report.getListOutput();
            if (listOutputs != null && !listOutputs.isEmpty()) {
                for (Report.ListOutput listOutput: listOutputs) {
                    processReportListOutput(listOutput);
                }
            }

        } finally {
            stack.pop();
        }

    }

    protected void processReportQuery(Report.Query query) {
        //no child complex types to recurse to
    }

    protected void processReportListOutput(Report.ListOutput listOutput) {
        //no child complex types to recurse to
    }
}
