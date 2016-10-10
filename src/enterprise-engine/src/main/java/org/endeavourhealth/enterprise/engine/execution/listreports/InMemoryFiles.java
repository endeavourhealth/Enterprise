package org.endeavourhealth.enterprise.engine.execution.listreports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

//This is only ever used from a single thread
public class InMemoryFiles {

    private final List<FileReference> inMemoryFilesList = new ArrayList<>();
    private final HashMap<UUID, HashMap<Integer, FileContentBuilder>> map = new HashMap<>();

    public FileContentBuilder getFileContentBuilder(UUID jobReportItemUuid, int groupId) {

        if (!map.containsKey(jobReportItemUuid))
            map.put(jobReportItemUuid, new HashMap<Integer, FileContentBuilder>());

        if (!map.get(jobReportItemUuid).containsKey(groupId)) {
            FileReference reference = new FileReference(jobReportItemUuid, groupId);
            inMemoryFilesList.add(reference);
            map.get(jobReportItemUuid).put(groupId, reference.getFileContentBuilder());
        }

        return map.get(jobReportItemUuid).get(groupId);
    }

    public List<FileReference> getAllFileReferences() {
        return inMemoryFilesList;
    }
}
