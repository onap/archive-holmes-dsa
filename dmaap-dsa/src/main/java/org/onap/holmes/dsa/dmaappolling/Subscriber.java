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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import lombok.Getter;
import lombok.Setter;
import org.glassfish.jersey.client.ClientConfig;
import org.onap.holmes.common.api.stat.VesAlarm;
import org.onap.holmes.common.dropwizard.ioc.utils.ServiceLocatorHolder;
import org.onap.holmes.common.exception.CorrelationException;

@Getter
@Setter
public class Subscriber {

    private DMaaPResponseUtil dMaaPResponseUtil = ServiceLocatorHolder.getLocator()
            .getService(DMaaPResponseUtil.class);

    /**
     * The number of milliseconds to wait for messages if none are immediately available. This
     * should normally be used, and set at 15000 or higher.
     */
    private int timeout = 15000;

    /**
     * The maximum number of messages to return
     */
    private int limit = 100;

    /**
     * The number of milliseconds to poll interval time. This should normally be used, and set at
     * 15000 or higher.
     */
    private int period = 15000;

    private boolean secure;
    private String topic;
    private String url;
    private String consumerGroup = "homlesGrounp1";
    private String consumer = "homlesGrounp1";
    private String authInfo;
    private String authExpDate;

    public List<VesAlarm> subscribe() throws CorrelationException {
        List<String> response;
        try {
            response = getDMaaPData();
        } catch (Exception e) {
            throw new CorrelationException("Failed to get DMapp data.", e);
        }
        try {
            return extractVesAlarm(response);
        } catch (Exception e) {
            throw new CorrelationException("Failed to convert the response data to VES alarms.", e);
        }
    }

    private List<String> getDMaaPData() {
        Client client = ClientBuilder.newClient(new ClientConfig());
        WebTarget webTarget = client.target(url + "/" + consumerGroup + "/" + consumer);
        Response response = webTarget.request().get();
        return response.readEntity(List.class);
    }

    private List<VesAlarm> extractVesAlarm(List<String> responseEntity) throws IOException {
        List<VesAlarm> vesAlarmList = new ArrayList<>();
        for (String entity : responseEntity) {
            vesAlarmList.add(dMaaPResponseUtil.convertJsonToVesAlarm(entity));
        }
        return vesAlarmList;
    }
}
