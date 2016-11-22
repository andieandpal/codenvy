/*
 *  [2012] - [2016] Codenvy, S.A.
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
package com.codenvy.api.license.server.dao;

import com.codenvy.api.license.model.Constants;
import com.codenvy.api.license.server.model.impl.CodenvyLicenseActionImpl;

import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;

import java.util.List;

/**
 * Defines data access object contract for {@link CodenvyLicenseActionImpl}.
 *
 * @author Anatolii Bazko
 */
public interface CodenvyLicenseDao {

    /**
     * Stores license action.
     *
     * @param codenvyLicenseAction
     *      any license action
     * @throws ConflictException
     *      if action already exists in the system
     * @throws ServerException
     *      any other error occurred
     */
    void store(CodenvyLicenseActionImpl codenvyLicenseAction) throws ServerException, ConflictException;

    /**
     * Removes codenvy license actions.
     *
     * @param licenseType
     *          {@link Constants.Type}
     * @throws ServerException
     *      any other error occurred
     */
    void remove(Constants.Type licenseType) throws ServerException;

    /**
     * Finds license action.
     *
     * @param licenseType
     *          {@link Constants.Type}
     * @param actionType
     *          {@link Constants.Action}
     * @return {@link CodenvyLicenseActionImpl}
     * @throws NotFoundException
     *      no license action found
     * @throws ServerException
     *      any other error occurred
     */
    CodenvyLicenseActionImpl getByLicenseAndType(Constants.Type licenseType, Constants.Action actionType) throws ServerException,
                                                                                                                 NotFoundException;

    /**
     * Finds license action.
     *
     * @param licenseType
     *          {@link Constants.Type}
     * @return {@link CodenvyLicenseActionImpl}
     * @throws ServerException
     *      any other error occurred
     */
    List<CodenvyLicenseActionImpl> getByLicense(Constants.Type licenseType) throws ServerException;

}
