/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2015] Codenvy, S.A.
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
package com.codenvy.analytics.metrics.subscription;

import com.codenvy.analytics.metrics.MetricFilter;
import com.codenvy.analytics.metrics.MetricType;
import com.codenvy.analytics.metrics.OmitFilters;
import com.codenvy.analytics.metrics.users.AbstractAccountActiveEntities;

import javax.annotation.security.RolesAllowed;

/** @author Anatoliy Bazko */
@RolesAllowed({"system/admin", "system/manager"})
@OmitFilters({MetricFilter.USER_ID, MetricFilter.REGISTERED_USER, MetricFilter.WS_ID, MetricFilter.PERSISTENT_WS})
public class CreditCardChargeFailed extends AbstractAccountActiveEntities {

    public CreditCardChargeFailed() {
        super(MetricType.CREDIT_CARD_CHARGE_FAILED, ACOUNT);
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return "The number of fails to charge credit cards";
    }
}
