/// <reference path="../../../typings/tsd.d.ts" />
/// <reference path="../../blocks/logger.service.ts" />

module app.dialogs {
    import IModalServiceInstance = angular.ui.bootstrap.IModalServiceInstance;
    import IModalSettings = angular.ui.bootstrap.IModalSettings;
    import IModalService = angular.ui.bootstrap.IModalService;
    import ExpressionType = app.models.ExpressionType;
    import Restriction = app.models.Restriction;
    import VariableType = app.models.VariableType;

    'use strict';

    export class ExpressionEditorController extends BaseDialogController {
        expressionText: string;

        expressionVariableName1: string;
        expressionRuleId1: string;
        expressionDataSource1: string;
        expressionRestrictionFieldName1: string;
        expressionRestrictionOrderDirection1: string;
        expressionRestrictionCount1: string;
        expressionTestField1: string;
        expressionFunction1: string;

        expressionVariableName2: string;
        expressionRuleId2: string;
        expressionDataSource2: string;
        expressionRestrictionFieldName2: string;
        expressionRestrictionOrderDirection2: string;
        expressionRestrictionCount2: string;
        expressionTestField2: string;
        expressionFunction2: string;

        newExpression : ExpressionType;

        editMode : boolean = false;

        sortorders = ['','ASCENDING','DESCENDING'];
        periods = ['','DAYS','WEEKS','MONTHS','YEARS'];
        rule = ['1','2'];
        fields = ['','EFFECTIVE_DATE','TIMESTAMP','VALUE'];
        functions = ['','AVERAGE','COUNT','MINIMUM','MAXIMUM'];

        public static open($modal : IModalService, expression : ExpressionType) : IModalServiceInstance {
            var options : IModalSettings = {
                templateUrl:'app/dialogs/expressionEditor/expressionEditor.html',
                controller:'ExpressionEditorController',
                controllerAs:'expressionEditor',
                size:'lg',
                backdrop: 'static',
                resolve:{
                    expression : () => expression
                }
            };

            var dialog = $modal.open(options);
            return dialog;
        }

        static $inject = ['$uibModalInstance', 'LoggerService', '$uibModal', 'expression'];

        constructor(protected $uibModalInstance : IModalServiceInstance,
                    private logger : app.blocks.ILoggerService,
                    private $modal : IModalService,
                    private expression: ExpressionType) {

            super($uibModalInstance);

            var vm = this;

            this.resultData = expression;

            vm.newExpression = {
                expressionText : "",
                variable : []
            };

            if (this.resultData.variable.length>0)
                this.initialiseEditMode(this.resultData);

        }

        initialiseEditMode(resultData : ExpressionType) {
            var vm = this;

            vm.editMode = true;

            if (resultData.variable === null) {
                resultData.variable = [];
            }

            vm.expressionText = resultData.expressionText;

            vm.expressionVariableName1 = resultData.variable[0].variableName;
            vm.expressionRuleId1 = resultData.variable[0].ruleId;
            vm.expressionDataSource1 = resultData.variable[0].ruleId;
            vm.expressionRestrictionFieldName1 = resultData.variable[0].restriction.fieldName;
            vm.expressionRestrictionOrderDirection1 = resultData.variable[0].restriction.orderDirection;
            vm.expressionRestrictionCount1 = resultData.variable[0].restriction.count.toString();
            vm.expressionTestField1 = resultData.variable[0].fieldName;
            vm.expressionFunction1 = resultData.variable[0].function;

            vm.expressionVariableName2 = resultData.variable[1].variableName;
            vm.expressionRuleId2 = resultData.variable[1].ruleId;
            vm.expressionDataSource2 = resultData.variable[1].ruleId;
            vm.expressionRestrictionFieldName2 = resultData.variable[1].restriction.fieldName;
            vm.expressionRestrictionOrderDirection2 = resultData.variable[1].restriction.orderDirection;
            vm.expressionRestrictionCount2 = resultData.variable[1].restriction.count.toString();
            vm.expressionTestField2 = resultData.variable[1].fieldName;
            vm.expressionFunction2 = resultData.variable[1].function;

        }

        save() {
            var vm = this;

            vm.resultData = vm.newExpression;

            vm.resultData.expressionText = vm.expressionText;

            var restriction : Restriction = {
                fieldName : vm.expressionRestrictionFieldName1,
                orderDirection : vm.expressionRestrictionOrderDirection1,
                count : Number(vm.expressionRestrictionCount1)
            }

            var variableType : VariableType = {
                variableName: "A",
                ruleId: vm.expressionDataSource1,
                restriction: restriction,
                fieldName: vm.expressionTestField1,
                function: vm.expressionFunction1
            };

            vm.resultData.variable.push(variableType);

            var restriction : Restriction = {
                fieldName : vm.expressionRestrictionFieldName2,
                orderDirection : vm.expressionRestrictionOrderDirection2,
                count : Number(vm.expressionRestrictionCount2)
            }

            var variableType : VariableType = {
                variableName: "B",
                ruleId: vm.expressionDataSource2,
                restriction: restriction,
                fieldName: vm.expressionTestField2,
                function: vm.expressionFunction2
            };

            vm.resultData.variable.push(variableType);

            this.ok();
        }


    }

    angular
        .module('app.dialogs')
        .controller('ExpressionEditorController', ExpressionEditorController);
}
