// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
package com.cloud.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.cloud.api.ApiServer;
import com.cloud.exception.InvalidParameterValueException;
import com.cloud.server.ConfigurationServer;
import com.cloud.server.ManagementServer;
import com.cloud.utils.SerialVersionUID;
import com.cloud.utils.component.ComponentContext;

public class CloudStartupServlet extends HttpServlet implements ServletContextListener {
    public static final Logger s_logger = Logger.getLogger(CloudStartupServlet.class.getName());

    static final long serialVersionUID = SerialVersionUID.CloudStartupServlet;

    @Override
    public void init() throws ServletException {
        ConfigurationServer c = (ConfigurationServer)ComponentContext.getComponent(ConfigurationServer.Name);
        try {
            c.persistDefaultValues();
            ManagementServer ms = (ManagementServer)ComponentContext.getComponent(ManagementServer.Name);
            ms.enableAdminUser("password");
            ApiServer.initApiServer();
        } catch (InvalidParameterValueException ipve) {
            s_logger.error("Exception starting management server ", ipve);
            throw new ServletException (ipve.getMessage());
        } catch (Exception e) {
            s_logger.error("Exception starting management server ", e);
            throw new ServletException (e.getMessage());
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            init();
        } catch (ServletException e) {
            s_logger.error("Exception starting management server ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
