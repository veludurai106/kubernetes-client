/**
 * Copyright (C) 2015 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fabric8.kubernetes.client.mock;

import io.fabric8.kubernetes.api.model.extensions.PodSecurityPolicy;
import io.fabric8.kubernetes.api.model.extensions.PodSecurityPolicyBuilder;
import io.fabric8.kubernetes.api.model.extensions.PodSecurityPolicyList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import static org.junit.Assert.*;

public class PodSecurityPolicyCrudTest {

  private static final Logger logger = LoggerFactory.getLogger(PodSecurityPolicyCrudTest.class);

  @Rule
  public KubernetesServer kubernetesServer = new KubernetesServer(true,true);

  @Test
  public void crudTest(){
    KubernetesClient client = kubernetesServer.getClient();

    PodSecurityPolicy podSecurityPolicy = new PodSecurityPolicyBuilder()
      .withNewMetadata()
      .withName("test-example")
      .endMetadata()
      .withNewSpec()
      .withPrivileged(false)
      .withNewRunAsUser().withRule("RunAsAny").endRunAsUser()
      .withNewFsGroup().withRule("RunAsAny").endFsGroup()
      .withNewSeLinux().withRule("RunAsAny").endSeLinux()
      .withNewSupplementalGroups().withRule("RunAsAny").endSupplementalGroups()
      .endSpec()
      .build();

    //test of Creation

    podSecurityPolicy = client.podSecurityPolicies().create(podSecurityPolicy);
    assertNotNull(podSecurityPolicy);
    assertEquals("test-example",podSecurityPolicy.getMetadata().getName());
    assertFalse(podSecurityPolicy.getSpec().getPrivileged());
    assertEquals("RunAsAny",podSecurityPolicy.getSpec().getRunAsUser().getRule());
    assertEquals("RunAsAny",podSecurityPolicy.getSpec().getFsGroup().getRule());
    assertEquals("RunAsAny",podSecurityPolicy.getSpec().getSeLinux().getRule());
    assertEquals("RunAsAny",podSecurityPolicy.getSpec().getSupplementalGroups().getRule());

    //test of list
    PodSecurityPolicyList podSecurityPolicyList = client.podSecurityPolicies().list();
    logger.info(podSecurityPolicyList.toString());

    assertNotNull(podSecurityPolicyList);
    assertEquals(1,podSecurityPolicyList.getItems().size());
    assertEquals("test-example",podSecurityPolicyList.getItems().get(0).getMetadata().getName());
    assertFalse(podSecurityPolicyList.getItems().get(0).getSpec().getPrivileged());
    assertEquals("RunAsAny",podSecurityPolicyList.getItems().get(0).getSpec().getRunAsUser().getRule());
    assertEquals("RunAsAny",podSecurityPolicyList.getItems().get(0).getSpec().getFsGroup().getRule());
    assertEquals("RunAsAny",podSecurityPolicyList.getItems().get(0).getSpec().getSeLinux().getRule());
    assertEquals("RunAsAny",podSecurityPolicyList.getItems().get(0).getSpec().getSupplementalGroups().getRule());

    //test of updation
    podSecurityPolicy = client.podSecurityPolicies().withName("test-example").edit()
        .editSpec().withPrivileged(true).endSpec()
        .done();

    logger.info("Updated PodSecurityPolicy : " + podSecurityPolicy.toString());

    assertNotNull(podSecurityPolicy);
    assertEquals("test-example",podSecurityPolicy.getMetadata().getName());
    assertTrue(podSecurityPolicy.getSpec().getPrivileged());
    assertEquals("RunAsAny",podSecurityPolicy.getSpec().getRunAsUser().getRule());
    assertEquals("RunAsAny",podSecurityPolicy.getSpec().getFsGroup().getRule());
    assertEquals("RunAsAny",podSecurityPolicy.getSpec().getSeLinux().getRule());
    assertEquals("RunAsAny",podSecurityPolicy.getSpec().getSupplementalGroups().getRule());

    //test of deletion
    boolean deleted = client.podSecurityPolicies().delete(podSecurityPolicy);
    assertTrue(deleted);
    podSecurityPolicyList = client.podSecurityPolicies().list();
    assertEquals(0,podSecurityPolicyList.getItems().size());

  }
}
