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

package org.onap.holmes.dsa.dmaap;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.onap.holmes.common.config.MQConfig;

public class DmaapDsaConfig extends Configuration {

    @NotEmpty
    private String defaultName = "ONAP Holmes DMaaP DSA";

    @NotEmpty
    private String apidescription = "Holmes data Source adapter API";

    @JsonProperty
    @NotNull
    @Valid
    private MQConfig mqConfig;


    public MQConfig getMqConfig() {
        return mqConfig;
    }

    public void setMqConfig(MQConfig mqConfig) {
        this.mqConfig = mqConfig;
    }

    public String getApidescription() {
        return apidescription;
    }

    public void setApidescription(String apidescription) {
        this.apidescription = apidescription;
    }
}
