/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.cli.command.builtin;

import com.codenvy.cli.command.builtin.model.UserProjectReference;
import com.codenvy.cli.preferences.Preferences;
import com.codenvy.client.Codenvy;
import com.codenvy.client.CodenvyBuilder;
import com.codenvy.client.CodenvyClient;
import com.codenvy.client.ProjectClient;
import com.codenvy.client.Request;
import com.codenvy.client.UserClient;
import com.codenvy.client.WorkspaceClient;
import com.codenvy.client.auth.Credentials;
import com.codenvy.client.auth.CredentialsBuilder;
import com.codenvy.client.auth.CredentialsProvider;
import com.codenvy.client.auth.Token;
import com.codenvy.client.auth.TokenBuilder;
import com.codenvy.client.model.Project;
import com.codenvy.client.model.User;
import com.codenvy.client.model.Workspace;
import com.codenvy.client.model.WorkspaceReference;

import org.apache.felix.service.command.CommandSession;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Super class of tests.
 *
 * @author Florent Benoit
 */
public abstract class AbsCommandTest {


    private MultiRemoteCodenvy multiRemoteCodenvy;

    @Mock
    private Preferences globalPreferences;

    @Mock
    private CommandSession commandSession;

    @Mock
    private CodenvyClient codenvyClient;

    @Mock
    private CredentialsBuilder credentialsBuilder;

    @Mock
    private Credentials credentials;

    @Mock
    private CredentialsProvider credentialsProvider;

    @Mock
    private TokenBuilder tokenBuilder;

    @Mock
    private Token token;


    @Mock
    private CodenvyBuilder codenvyBuilder;

    @Mock
    private Codenvy codenvy;

    @Mock
    private UserClient userClient;

    @Mock
    private User user;

    @Mock
    private Request<User> userRequest;

    @Mock
    private ProjectClient projectClient;

    @Mock
    private Request<List<Project>> projectRequests;

    @Mock
    private WorkspaceClient workspaceClient;

    @Mock
    private Request<List<Workspace>> workspaceRequests;

    private List<Workspace> workspaces;

    /**
     * List of projects for a given project name
     */
    private Map<String, List<Project>> projects;


    @BeforeClass
    public void initMocks() {
        MockitoAnnotations.initMocks(this);

        // We shouldn't use session.getConsole() for logging as grep, less, more commands won't work in interactive mode
        when(commandSession.getConsole()).thenAnswer(
                new Answer() {
                    @Override
                    public Object answer(InvocationOnMock invocation) {
                        throw new IllegalStateException("System.out.println should be used instead");
                    }
                });


        doReturn(credentials).when(credentialsBuilder).build();
        doReturn(credentialsBuilder).when(credentialsBuilder).withPassword(anyString());
        doReturn(credentialsBuilder).when(credentialsBuilder).withUsername(anyString());
        doReturn(credentialsBuilder).when(credentialsBuilder).withToken(any(Token.class));
        doReturn(credentialsBuilder).when(getCodenvyClient()).newCredentialsBuilder();

        doReturn(token).when(tokenBuilder).build();
        doReturn(token).when(credentials).token();
        doReturn(tokenBuilder).when(getCodenvyClient()).newTokenBuilder(anyString());


        doReturn(codenvyBuilder).when(getCodenvyClient()).newCodenvyBuilder(anyString(), anyString());
        doReturn(codenvyBuilder).when(codenvyBuilder).withCredentials(credentials);
        doReturn(codenvyBuilder).when(codenvyBuilder).withCredentialsProvider(any(CredentialsProvider.class));
        doReturn(codenvy).when(codenvyBuilder).build();

        // UserClient
        doReturn(userRequest).when(userClient).current();
        doReturn(user).when(userRequest).execute();
        doReturn(userClient).when(codenvy).user();

        // WorkspaceClient
        doReturn(workspaceClient).when(codenvy).workspace();
        doReturn(workspaceRequests).when(getWorkspaceClient()).all();
        // workspaces to use
        this.workspaces = new ArrayList<>();
        doReturn(workspaces).when(workspaceRequests).execute();

        // ProjectClient
        this.projects = new HashMap<>();
        doReturn(projectClient).when(codenvy).project();

        // intercept request
        when(projectClient.getWorkspaceProjects(anyString())).thenAnswer(
                new Answer() {
                    @Override
                    public Object answer(InvocationOnMock invocation) {
                        final String workspaceName =
                                invocation.getArguments()[0].toString();

                        Request<List<Project>> requestProject =
                                mock(Request.class);

                        when(requestProject.execute()).thenAnswer(new Answer<Object>() {
                            @Override
                            public Object answer(InvocationOnMock invocation) throws Throwable {
                                List<Project> workspaceProjects =
                                        projects.get(workspaceName);
                                if (workspaceProjects == null) {
                                    workspaceProjects =
                                            new ArrayList<Project>();
                                    projects.put(workspaceName,
                                                 workspaceProjects);
                                }

                                return workspaceProjects;
                            }
                        });

                        return requestProject;

                    }
                });

        doReturn(codenvy).when(commandSession).get(Codenvy.class.getName());

        this.multiRemoteCodenvy = Mockito.spy(new MultiRemoteCodenvy(codenvyClient, globalPreferences, commandSession));

    }

    protected CommandSession getCommandSession() {
        return commandSession;
    }

    /**
     * Prepare the given command by injecting default configuration
     *
     * @param command
     */
    protected void prepare(AbsCommand command) {
        command.setCodenvyClient(codenvyClient);
        doReturn(multiRemoteCodenvy).when(commandSession).get(MultiRemoteCodenvy.class.getName());
        doReturn(globalPreferences).when(commandSession).get(Preferences.class.getName());

        // intercept getProjects() method
        doReturn(multiRemoteCodenvy.getProjects("default", codenvy)).when(multiRemoteCodenvy).getProjects();
        doReturn(true).when(multiRemoteCodenvy).hasReadyRemotes();
        doReturn(true).when(multiRemoteCodenvy).hasAvailableRemotes();
        doReturn(Collections.emptyList()).when(multiRemoteCodenvy).getBuilders(any(UserProjectReference.class));
        doReturn(Collections.emptyList()).when(multiRemoteCodenvy).getRunners(any(UserProjectReference.class));
        doReturn(Collections.emptyList()).when(multiRemoteCodenvy).getProjectPermissions(any(UserProjectReference.class));

    }

    protected CodenvyClient getCodenvyClient() {
        return codenvyClient;
    }

    protected CredentialsBuilder getCredentialsBuilder() {
        return credentialsBuilder;
    }

    protected Credentials getCredentials() {
        return credentials;
    }

    protected Token getToken() {
        return token;
    }

    protected CodenvyBuilder getCodenvyBuilder() {
        return codenvyBuilder;
    }

    protected User getUser() {
        return user;
    }

    protected UserClient getUserClient() {
        return userClient;
    }

    public Codenvy getCodenvy() {
        return codenvy;
    }

    public Request<User> getRequest() {
        return userRequest;
    }

    public ProjectClient getProjectClient() {
        return projectClient;
    }

    public WorkspaceClient getWorkspaceClient() {
        return workspaceClient;
    }

    public Request<List<Workspace>> getWorkspaceRequests() {
        return workspaceRequests;
    }

    public List<Workspace> getWorkspaces() {
        return workspaces;
    }


    protected Workspace addWorkspace(String workspaceName) {
        Workspace workspace = mock(Workspace.class);
        WorkspaceReference workspaceRef = mock(WorkspaceReference.class);
        doReturn(workspaceRef).when(workspace).workspaceReference();
        doReturn(workspaceName).when(workspaceRef).name();
        doReturn(workspaceName).when(workspaceRef).id();

        getWorkspaces().add(workspace);

        Request<WorkspaceReference> requestWorkspaceRef = mock(Request.class);
        doReturn(requestWorkspaceRef).when(getWorkspaceClient()).withName(workspaceName);
        doReturn(workspaceRef).when(requestWorkspaceRef).execute();

        return workspace;
    }

    protected Project addProject(String workspaceName, String projectName) {
        Project project = mock(Project.class);
        doReturn(projectName).when(project).name();
        doReturn(String.valueOf(true)).when(project).visibility();
        doReturn("java").when(project).projectTypeId();

        getProjects(workspaceName).add(project);

        return project;
    }

    protected List<Project> getProjects(String workspaceName) {
        List<Project> workspaceProjects = projects.get(workspaceName);
        if (workspaceProjects == null) {
            workspaceProjects = new ArrayList<>();
            projects.put(workspaceName, workspaceProjects);
        }
        return workspaceProjects;
    }


}
