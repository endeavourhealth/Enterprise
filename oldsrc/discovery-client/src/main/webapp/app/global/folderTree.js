/*global define, $*/

define([
        'jstree'],
    function () {
        "use strict";

        var ctor = function (moduleIdParameter) {
            var moduleId = moduleIdParameter;
            var folders;

            function getInitialisePromise() {
                return $.ajax({
                    url: '/rest/folders/root/' + moduleId,
                    type: "GET"
                }).done(function (data) {
                    folders = parseFolders(data);

                    if (folders.length !== 0) {
                        folders[0].state = {};
                        folders[0].state.selected = true;
                    }
                });
            }

            function attached() {
                var parameters = {
                    core: {
                        multiple : false,
                        data : function (obj, callback) {
                            if (obj.id === '#')
                                callback.call(this, folders);
                            else {
                                var children = requestChildFolders(obj.id);
                                callback.call(this, children);
                            }
                        },
                        themes : {
                            dots : false
                        }
                    }
                };

                var treeDom = $('#folder-tree');

                treeDom.jstree(parameters);
                treeDom.on('changed.jstree', function (e, data) {
                    if (vm.onFolderSelected) {
                        if (data.selected.length === 0)
                            vm.onFolderSelected(null);
                        else
                            vm.onFolderSelected(data.selected[0]);
                    }
                });

                ////reselect.jstree
                //treeDom.on("loaded.jstree", function (event, data) {
                //    var tree = data.instance;
                //    var obj = tree.get_selected(true)[0];
                //
                //    if (obj)
                //        tree.select_node(obj);
                //});
            }

            function requestChildFolders(folderId) {
                var parentFolder = findFolder(folderId);

                if (!parentFolder)
                    return null;

                var childFolders;

                var promise = $.ajax({
                    url: '/rest/folders/childFolders/' + folderId,
                    type: "GET",
                    async: false
                }).done(function (data) {
                    childFolders = parseFolders(data);
                });

                parentFolder.children = childFolders;
                return childFolders;
            }

            function parseFolders(sourceArray) {
                var targetArray = [];

                if (!sourceArray)
                    return targetArray;

                for (var i = 0; i < sourceArray.length; i++) {
                    var source = sourceArray[i];

                    var target = {
                        id: source.folderGuid,
                        text: source.name
                        //icon        : "string" // string for custom
                    };

                    if (source.hasChildren)
                        target.children = true;
                    else
                        target.children = [];

                    targetArray.push(target);
                }

                return targetArray;
            }


            function getSelectedFolder() {
                var currentNode = $('#folder-tree').jstree('get_selected');

                if (!currentNode || currentNode.length !== 1)
                    return null;

                var folderId = currentNode[0];

                var folder = findFolder(folderId);
                return folder;
            }


            function findFolder(folderGuid) {
                return findFolderRecursive(folders, folderGuid);
            }

            function findFolderRecursive(folders, folderGuid) {
                if ($.isArray(folders)) {
                    for (var i = 0; i < folders.length; i++) {
                        var folder = folders[i];

                        if (folder.id === folderGuid)
                            return folder;
                        else {
                            var x = findFolderRecursive(folder.children, folderGuid);

                            if (x)
                                return x;
                        }
                    }
                }

                return null;
            }

            var vm = {
                getInitialisePromise: getInitialisePromise,
                attached: attached,
                onFolderSelected: null
            };

            return vm;
        };

        return ctor;
    }
);