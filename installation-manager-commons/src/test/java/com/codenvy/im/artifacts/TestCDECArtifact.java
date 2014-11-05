/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.im.artifacts;

import com.codenvy.im.utils.HttpTransport;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.endsWith;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/** @author Anatoliy Bazko */
public class TestCDECArtifact {
    private Artifact      cdecArtifact;
    private HttpTransport transport;

    @BeforeMethod
    public void setUp() throws Exception {
        transport = mock(HttpTransport.class);
        cdecArtifact = new CDECArtifact("", transport);
    }

    @Test
    public void testInstalledVersion() throws Exception {
        when(transport.doOption(endsWith("api/"), eq("authToken"))).thenReturn("{ideVersion:3.2.0-SNAPSHOT}");

        String version = cdecArtifact.getInstalledVersion("authToken");
        assertEquals(version, "3.2.0-SNAPSHOT");
    }
}
