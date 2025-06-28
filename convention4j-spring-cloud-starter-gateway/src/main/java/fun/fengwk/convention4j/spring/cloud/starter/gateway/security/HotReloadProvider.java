/*
 * Copyright 2017-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fun.fengwk.convention4j.spring.cloud.starter.gateway.security;

import lombok.extern.slf4j.Slf4j;

import java.security.Provider;

/**
 * @author fengwk
 */
@Slf4j
public final class HotReloadProvider extends Provider {

    private static final long serialVersionUID = 1L;

    public HotReloadProvider() {
        super("Hot Reload", 1.0, "Hot Reload KeyManagerFactory Provider");
        put("KeyManagerFactory.SunX509", HotReloadKeyManagerFactory.SunX509.class.getName());
        put("KeyManagerFactory.NewSunX509", HotReloadKeyManagerFactory.NewSunX509.class.getName());
        put("Alg.Alias.KeyManagerFactory.PKIX", "NewSunX509");
        log.info("Hot Reload KeyManager Loaded");
    }

}
