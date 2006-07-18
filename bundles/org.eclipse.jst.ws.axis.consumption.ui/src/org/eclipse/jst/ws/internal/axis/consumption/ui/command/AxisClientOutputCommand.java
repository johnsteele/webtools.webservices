/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.ws.internal.axis.consumption.ui.command;


import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.ws.internal.wsrt.IContext;
import org.eclipse.wst.ws.internal.wsrt.IWebServiceClient;


public class AxisClientOutputCommand extends AbstractDataModelOperation {
	
	private IWebServiceClient wsc_;
	private String proxyBean_;
	  
		/**
		* Default CTOR
		*/
		public AxisClientOutputCommand() {
		}
		
		public AxisClientOutputCommand(IWebServiceClient wsc, IContext context) {
			wsc_ = wsc;
		}
		
	public IStatus execute( IProgressMonitor monitor, IAdaptable adaptable ) 
	{	
		wsc_.getWebServiceClientInfo().setImplURL(proxyBean_);
		return Status.OK_STATUS;
	  }

	public void setProxyBean(String proxyBean) {
		this.proxyBean_ = proxyBean;
	}
	  
		
}