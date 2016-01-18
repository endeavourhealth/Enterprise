/*global define, $*/

define([
        'knockout',
        'global/folderTree',
        'global/tableWrapper',
        'global/uiMapper',
        'moment',
        'global/globalState',
        'plugins/router'
    ],
    function (
        ko,
        FolderTree,
        TableWrapper,
        uiMapper,
        moment,
        globalState,
        router
    ) {
        "use strict";

        var initialised = false;
        var folderTree;
        var tableWrapper;

        function activate() {

            var promise;

            if (!initialised) {
                //promise = $.ajax({
                //    url: '/rest/library/initialise',
                //    type: "GET"
                //}).done(function (data) {
                //    folders = parseFolders(data);
                //});

                tableWrapper = new TableWrapper('main-table', createTableOptions());

                folderTree = new FolderTree(0);
                folderTree.onFolderSelected = onFolderSelected;

                var folderTreePromise = folderTree.getInitialisePromise();

                promise = folderTreePromise;

                initialised = true;
            } else {
                promise = $.Deferred().resolve().promise();
            }

            return promise;
        }

        function attached() {
            folderTree.attached();
        }

        function deactivate() {
            tableWrapper.destroy();
        }

        function addFolderRequested() {

        }

        function addQueryRequested() {
            globalState.queryEditor.visible(true);

            router.navigate('queryEditor');
        }

        function addCodeSetRequested() {

        }

        function editRequested() {
            //var currentFolder = getSelectedFolder();
            //vm.folderToEdit(currentFolder);
            //
            //$('#folder-editor').modal();
        }

        function onFolderSelected(folderUuid) {

            var promise = $.ajax({
                url: '/rest/folders/content/' + folderUuid,
                type: "GET"
            }).done(function (data) {

                var folderContentHierarchy = createTableContentHierarchy(data);
                var folderContent = flattenTableContent(folderContentHierarchy);

                tableWrapper.draw(folderContent);

            });
        }


        //---------------------------------------MAIN TABLE-------------------------------------
        //--------------------------------------------------------------------------------------

        function createTableContentHierarchy(data) {
            if (!data || data.length === 0)
                return [];

            var dictionary = {};
            var rootItems = {};

            for (var i = 0; i < data.length; i++) {
                var dataItem = data[i];
                var tableItem = new TableItem(dataItem.itemUuid, dataItem.title, dataItem.auditDateTime, dataItem.itemType);
                dictionary[tableItem.uuid] = tableItem;
                rootItems[tableItem.uuid] = tableItem;
            }

            for (var j = 0; j < data.length; j++) {
                var dataItem2 = data[j];
                if (!dataItem2.parentUuid)
                    continue;

                if (dictionary[dataItem2.parentUuid]) {
                    dictionary[dataItem2.parentUuid].children.push(dictionary[dataItem2.itemUuid]);
                    rootItems[dataItem2.itemUuid] = undefined;  //much faster than delete
                }
            }

            var finalArray = [];

            for (var key in rootItems) {
                if (rootItems.hasOwnProperty(key) && rootItems[key]) {
                    finalArray.push(rootItems[key]);
                }
            }

            return finalArray;
        }

        function flattenTableContent(inputArray) {

            var finalArray = [];
            flattenTableContentRecursive(inputArray, finalArray, 0);
            return finalArray;
        }

        function flattenTableContentRecursive(inputArray, finalArray, indent) {

            for (var i = 0; i < inputArray.length; i++) {
                var dataItem = inputArray[i];

                if (indent > 0)
                    dataItem.displayName = createSpaces(indent) + dataItem.name;

                finalArray.push(dataItem);

                if (dataItem.children.length !== 0)
                    flattenTableContentRecursive(dataItem.children, finalArray, indent + 1);
            }
        }

        function createSpaces(count) {
            count = count * 5;
            return new Array(count + 1).join( "&nbsp;" );
        }

        function TableItem(uuid, name, dateModified, type) {
            this.uuid = uuid;
            this.name = name;
            this.dateModified = dateModified;
            this.type = type;
            this.displayName = name;
            this.children = [];
        }

        function createTableOptions() {
            return {
                paging: false,
                ordering: false,
                info: false,
                searching: false,
                scrollX: false,
                scrollY: true,
                select: true,
                columns: [
                    { data: 'displayName' },
                    {
                        data: null,
                        render: function (data, type, full, meta) {
                            var mom = moment([full.dateModified.year, full.dateModified.monthOfYear, full.dateModified.dayOfMonth, full.dateModified.hourOfDay, full.dateModified.minuteOfHour]);
                            return mom.format('DD/MM/YYYY hh:mm');
                        }
                    },
                    {
                        data: null,
                        render: function (data, type, full, meta) {
                            return uiMapper.itemTypeForDisplay(full.type);
                        }
                    }
                ]
            };
        }


        //=------------------------------------------END----------------------------------------
        //--------------------------------------------------------------------------------------

        var vm = {
            activate: activate,
            attached: attached,
            deactivate: deactivate,

            addQueryRequested: addQueryRequested,
            addFolderRequested: addFolderRequested,
            addCodeSetRequested: addCodeSetRequested,

            editRequested: editRequested,
            folderToEdit: ko.observable()
        };

        return vm;
    }
);