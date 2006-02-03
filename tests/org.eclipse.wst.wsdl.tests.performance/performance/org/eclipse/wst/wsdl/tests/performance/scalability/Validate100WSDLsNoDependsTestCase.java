/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.wsdl.tests.performance.scalability;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.common.tests.performance.internal.scalability.RunValidatorTestCase;
import org.eclipse.wst.ws.internal.plugin.WSPlugin;
import org.eclipse.wst.ws.internal.preferences.PersistentWSIContext;

public class Validate100WSDLsNoDependsTestCase extends RunValidatorTestCase
{
	protected String getValidatorId()
	{
	    return "org.eclipse.wst.wsdl.ui.internal.validation.Validator";
	}

	protected String getBundleId()
	{
	    return "org.eclipse.wst.wsdl.tests.performance";
	}
	
	protected void setUp() throws Exception
	{
		WSPlugin.getDefault().getWSIAPContext().updateWSICompliances(PersistentWSIContext.IGNORE_NON_WSI);
		WSPlugin.getDefault().getWSISSBPContext().updateWSICompliances(PersistentWSIContext.IGNORE_NON_WSI);
	    IProject project = createProject("sp");
	    String bundleId = getBundleId();
	    for(int i = 1; i < 100; i++)
	    {
	      copyFile(bundleId, "data/100WSDLsNoDepends/sample" + i +".wsdl", project);
	    }
	    super.setUp();
	}

	protected String getFilePath()
	{
	    return "data/100WSDLsNoDepends/sample0.xsd";
	}
	
	public void testValidate100WSDLsNoDepends()
	{
	    try
	    {
	      super.execute();
	    }
	    catch (Throwable t)
	    {
	      fail(t.getMessage());
	    }
	}

}