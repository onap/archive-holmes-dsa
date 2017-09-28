/*
 * Copyright 2017 ZTE Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onap.holmes.dsa.dmaappolling;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jvnet.hk2.annotations.Service;
import org.onap.holmes.common.api.stat.AlarmAdditionalField;
import org.onap.holmes.common.api.stat.VesAlarm;

@Service
public class DMaaPResponseUtil {

    public VesAlarm convertJsonToVesAlarm(String responseJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(responseJson);
        VesAlarm vesAlarm = new VesAlarm();

        JsonNode eventJson = jsonNode.get("event");

        JsonNode commonEventHeaderJson = eventJson.get("commonEventHeader");
        convertCommonEventHeaderJsonToEvent(commonEventHeaderJson, vesAlarm);

        JsonNode faultFieldsJson = eventJson.get("faultFields");
        convertFaultFieldsJsonToEvent(faultFieldsJson, vesAlarm);
        return vesAlarm;
    }

    private void convertCommonEventHeaderJsonToEvent(JsonNode commonEventHeaderJson,
            VesAlarm vesAlarm) {
        vesAlarm.setDomain(commonEventHeaderJson.get("domain").asText());
        vesAlarm.setEventId(commonEventHeaderJson.get("eventId").asText());
        vesAlarm.setEventName(commonEventHeaderJson.get("eventName").asText());
        vesAlarm.setAlarmIsCleared(vesAlarm.getEventName().endsWith("Cleared") ? 1 : 0);
        vesAlarm.setEventType(getTextElementByNode(commonEventHeaderJson, "eventType"));
        vesAlarm.setInternalHeaderFields(
                getTextElementByNode(commonEventHeaderJson, "internalHeaderFields"));
        vesAlarm.setLastEpochMicrosec(commonEventHeaderJson.get("lastEpochMicfrosec").asLong());
        vesAlarm.setNfcNamingCode(getTextElementByNode(commonEventHeaderJson, "nfcNamingCode"));
        vesAlarm.setNfNamingCode(getTextElementByNode(commonEventHeaderJson, "nfNamingCode"));
        vesAlarm.setPriority(commonEventHeaderJson.get("priority").asText());
        vesAlarm.setReportingEntityId(
                getTextElementByNode(commonEventHeaderJson, "reportingEntityId"));
        vesAlarm.setReportingEntityName(commonEventHeaderJson.get("reprotingEntityName").asText());
        vesAlarm.setSequence(commonEventHeaderJson.get("sequence").asInt());
        vesAlarm.setSourceId(getTextElementByNode(commonEventHeaderJson, "sourceId"));
        vesAlarm.setSourceName(commonEventHeaderJson.get("sourceName").asText());
        vesAlarm.setStartEpochMicrosec(commonEventHeaderJson.get("startEpochMicrosec").asLong());
        vesAlarm.setVersion(commonEventHeaderJson.get("version").asLong());
    }

    private void convertFaultFieldsJsonToEvent(JsonNode faultFieldsJson, VesAlarm vesAlarm) {
        vesAlarm.setAlarmAdditionalInformation(getListElementByNode(faultFieldsJson, "alarmAdditionalInformation"));
        vesAlarm.setAlarmCondition(faultFieldsJson.get("alarmCondition").asText());
        vesAlarm.setAlarmInterfaceA(getTextElementByNode(faultFieldsJson, "alarmInterfaceA"));
        vesAlarm.setEventCategory(getTextElementByNode(faultFieldsJson,"eventCategory"));
        vesAlarm.setEventSeverity(faultFieldsJson.get("eventSeverity").asText());
        vesAlarm.setEventSourceType(faultFieldsJson.get("eventSourceType").asText());
        vesAlarm.setFaultFieldsVersion(faultFieldsJson.get("faultFieldsVersion").asLong());
        vesAlarm.setSpecificProblem(faultFieldsJson.get("specificProblem").asText());
        vesAlarm.setVfStatus(faultFieldsJson.get("vfStatus").asText());
    }

    private String getTextElementByNode(JsonNode jsonNode,String name){
        if(jsonNode.has(name)){
            return jsonNode.get(name).asText();
        }
        return null;
    }

    private Long getLongElementByNode(JsonNode jsonNode, String name) {
        if(jsonNode.has(name)){
            return jsonNode.get(name).asLong();
        }
        return null;
    }

    private List<AlarmAdditionalField> getListElementByNode(JsonNode jsonNode, String name){
        List<AlarmAdditionalField> alarms = new ArrayList<AlarmAdditionalField>();
        if (jsonNode.has(name)) {
            JsonNode alarmAdditionalInformations = jsonNode.get(name);
            if (alarmAdditionalInformations.isArray()) {
                alarmAdditionalInformations.forEach(alarm -> {
                    if(alarm.has("name") && alarm.has("value")) {
                        AlarmAdditionalField field = new AlarmAdditionalField();
                        field.setName(getTextElementByNode(alarm, "name"));
                        field.setName(getTextElementByNode(alarm, "value"));
                        alarms.add(field);
                    }
                });
            }
        }
        return alarms;
    }
}
