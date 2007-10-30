/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 * yyyymmdd bug      Email and other contact information
 * -------- -------- -----------------------------------------------------------
 * 20071024   196997 pmoogk@ca.ibm.com - Peter Moogk
 *******************************************************************************/
package org.eclipse.wst.ws.service.policy;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.ws.service.internal.policy.ServicePolicyPlatformImpl;

public class ServicePolicyPlatform
{
  private static ServicePolicyPlatform instance;
  private ServicePolicyPlatformImpl platformImpl;
  
  private ServicePolicyPlatform()
  {
    platformImpl = new ServicePolicyPlatformImpl();
  }
  
  public static ServicePolicyPlatform getInstance()
  {
    if( instance == null )
    {
      instance = new ServicePolicyPlatform();
    }
    
    return instance;
  }
  
  public void commitChanges()
  {
    platformImpl.commitChanges();
  }
  
  public void discardChanges()
  {
    platformImpl.discardChanges(); 
  }
  
  public void commitChanges( IProject project )
  {
    platformImpl.commitChanges( project );
  }
  
  public void discardChanges( IProject project )
  {
    platformImpl.discardChanges( project ); 
  }
  
  public Set<String> getAllPolicyIds()
  {
    return platformImpl.getAllPolicyIds();
  }
  
  public List<IServicePolicy> getRootServicePolicies( IFilter filter )
  {
    return platformImpl.getRootServicePolicies( filter );
  }
  
  public IServicePolicy getServicePolicy( String id )
  {
    return platformImpl.getServicePolicy( id );  
  }
  
  public boolean isProjectPreferencesEnabled( IProject project )
  {
    return platformImpl.isProjectPreferencesEnabled( project );
  }
  
  public void setProjectPreferencesEnabled( IProject project, boolean value )
  {
    platformImpl.setProjectPreferencesEnabled( project, value ); 
  }
  
  public void restoreDefaults()
  {
    platformImpl.restoreDefaults();
  }
  
  public void restoreDefaults( IProject project )
  {
    platformImpl.restoreDefaults( project );
  }
  
  /**
   * This method creates an IServicePolicy object.
   * 
   * @param parent the parent policy for this policy.  If this policy has no 
   * parent null may be specified.
   * @param id This is a unique id for this service policy.  If the id specified
   * is not unique trailing numerical digits in the id will be stripped off.  
   * Numerical digits will then be added to end of the id to make it unique.
   * If the id is empty or null the framework will assign a unique id.
   * @param enumListId If this policy's state is defined by an enumeration
   * the enumeration id should be specified here.  Otherwise null should be
   * specified.
   * @param defaultEnumId If this policy's state is defined by an enumeration
   * this parameter specifies the default value.  This value may be null 
   * if not using an enumeration or if the default value of the enumeration
   * should be used.
   * @return returns a service policy object.
   */
  public IServicePolicy createServicePolicy( IServicePolicy parent, String id, String enumListId, String defaultEnumId )
  {
    return platformImpl.createServicePolicy( parent, id, enumListId, defaultEnumId );
  }
  
  public List<IStateEnumerationItem> getStateEnumeration( String enumId )
  {
    return platformImpl.getStateEnumeration( enumId ); 
  }
  
  public IStateEnumerationItem getStateItemEnumeration( String stateItemId )
  {
    return platformImpl.getStateItemEnumeration( stateItemId );
  }
}
