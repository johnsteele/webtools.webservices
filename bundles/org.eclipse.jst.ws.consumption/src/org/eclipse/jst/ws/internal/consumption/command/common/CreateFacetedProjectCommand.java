/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 * yyyymmdd bug      Email and other contact information
 * -------- -------- -----------------------------------------------------------
 * 20060131 121071   rsinha@ca.ibm.com - Rupam Kuehner     
 * 20060204 124408   rsinha@ca.ibm.com - Rupam Kuehner      
 *******************************************************************************/

package org.eclipse.jst.ws.internal.consumption.command.common;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.jst.server.core.FacetUtil;
import org.eclipse.jst.ws.internal.common.ServerUtils;
import org.eclipse.jst.ws.internal.consumption.ConsumptionMessages;
import org.eclipse.jst.ws.internal.consumption.common.FacetMatcher;
import org.eclipse.jst.ws.internal.consumption.common.FacetSetsByTemplateCache;
import org.eclipse.jst.ws.internal.consumption.common.FacetUtils;
import org.eclipse.jst.ws.internal.consumption.common.RequiredFacetVersion;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.command.internal.env.core.common.StatusUtils;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.ServerCore;

public class CreateFacetedProjectCommand extends AbstractDataModelOperation
{

  private String   projectName;
  private String   templateId;
  private RequiredFacetVersion[]   requiredFacetVersions;
  //private FacetMatcher facetMatcher;
  private String   serverFactoryId;
  private String   serverInstanceId;
  
  private org.eclipse.wst.common.project.facet.core.runtime.IRuntime facetRuntime;
  
  private IProgressMonitor monitor_;
  
  
  public IStatus execute(IProgressMonitor monitor, IAdaptable info)
  {
    monitor_ = monitor;
    IStatus status = Status.OK_STATUS;
    
    // check if data ready
    status = checkDataReady();
    if (status.getSeverity()==Status.ERROR)
    {
      return status;
    }

    IProject project = ProjectUtilities.getProject(projectName);
    if (!project.exists())
    {
      try
      {
        IFacetedProject fproject = ProjectFacetsManager.create(projectName, null, monitor_);
        
        //Decide which facets to install based on the templateId and the selected server. 
        Set facetsToAdd = getFacetsToAdd();
        
        //Set up the install actions.
        Set actions = FacetUtils.getInstallActions(facetsToAdd);
        fproject.modify(actions, monitor_);
        
        Set newFacetVersions = fproject.getProjectFacets();
        Set fixedFacets = new HashSet();
        for (Iterator iter = newFacetVersions.iterator(); iter.hasNext();) {
            IProjectFacetVersion facetVersion = (IProjectFacetVersion) iter.next();
            fixedFacets.add(facetVersion.getProjectFacet());
        }
        fproject.setFixedProjectFacets(fixedFacets);
        
        
        //Set the runtime        
        if (facetRuntime != null)
        {
          fproject.setRuntime(facetRuntime, monitor);
        }

 
      } catch (CoreException ce)
      {
        return StatusUtils.errorStatus(NLS.bind(ConsumptionMessages.MSG_ERROR_PROJECT_CREATION, new String[] { projectName }));
      }
    }
    return status;
  }
    
  private IStatus checkDataReady()
  {

    if (projectName == null || serverFactoryId == null)
    {
      return StatusUtils.errorStatus(NLS.bind(ConsumptionMessages.MSG_ERROR_PROJECT_CREATION, new String[] {projectName}));
    }

    return Status.OK_STATUS;
  }
  
  /*
   * @return Set Returns the Set of facets to add to the new project, 
   * choosing the highest level of each facet that works on the selected server.
   */
  private Set getFacetsToAdd()
  {
    Set facets = null;
    
    //Set the facet runtime.
    setFacetRuntime();
    Set[] allCombinations = FacetSetsByTemplateCache.getInstance().getFacetVersionCombinationsFromTemplate(templateId);
    int n = allCombinations.length;
    if (facetRuntime != null)
    {
      for (int i=n-1; i>=0; i--)
      {
        //Check this template combination to see if it is compatible with both the 
        //service/client runtime and the server runtime. If it is, choose it.
        Set combination = allCombinations[i];
        FacetMatcher fm = FacetUtils.match(requiredFacetVersions, combination);
        if (fm.isMatch())
        {
          //Check against Runtime
          if (FacetUtils.doesRuntimeSupportFacets(facetRuntime, combination))
          {
            //We have a match. Use this combination of facet versions for project creation.
            facets = combination;
            break;
          }
        }                
      }
    }
    else
    {
      for (int i=n-1; i>=0; i--)
      {
        //Check this template combination to see if it is compatible with both the 
        //service/client runtime and the server runtime. If it is, choose it.
        Set combination = allCombinations[i];
        FacetMatcher fm = FacetUtils.match(requiredFacetVersions, combination);
        if (fm.isMatch())
        {
            //We have a match. Use this combination of facet versions for project creation.
            facets = combination;
            break;
        }                
      }      
    }
   
    if (facets == null)
    {
      facets = FacetUtils.getInitialFacetVersionsFromTemplate(templateId);
    }
     
    return facets;
  }

  private void setFacetRuntime()
  {
    
    if (serverInstanceId != null && serverInstanceId.length()>0)
    {
      IServer server = ServerCore.findServer(serverInstanceId);
      IRuntime sRuntime = server.getRuntime();
      facetRuntime = FacetUtil.getRuntime(sRuntime);      
    }
    else
    {
      //Find a non Stub runtime that matches this server type
      IRuntime serverRuntime = ServerUtils.getNonStubRuntime(serverFactoryId);
      if (serverRuntime != null)
      {
        facetRuntime = FacetUtil.getRuntime(serverRuntime);
      }
      else
      {
        //Accept stub runtime.
        IServerType st = ServerCore.findServerType(serverFactoryId);
        String runtimeTypeId = st.getRuntimeType().getId();   
        //Find the facet runtime
        IRuntime[] runtimes = ServerCore.getRuntimes();
        for (int i=0; i<runtimes.length; i++)
        {
          IRuntime sRuntime = runtimes[i];
          if (sRuntime.getRuntimeType().getId().equals(runtimeTypeId))
          {
            facetRuntime = FacetUtil.getRuntime(sRuntime);
          }
        }                
      }
    }
  }
  
  /*
  public void setFacetMatcher(FacetMatcher facetMatcher)
  {
    this.facetMatcher = facetMatcher;
  }
  */
  
  public void setProjectName(String projectName)
  {
    this.projectName = projectName;
  }  
  
  public void setTemplateId(String templateId)
  {
    this.templateId = templateId;
  }    

  public void setRequiredFacetVersions(RequiredFacetVersion[] requiredFacetVersions)
  {
    this.requiredFacetVersions = requiredFacetVersions;
  }

  public void setServerFactoryId(String serverFactoryId)
  {
    this.serverFactoryId = serverFactoryId;
  }

  public void setServerInstanceId(String serverInstanceId)
  {
    this.serverInstanceId = serverInstanceId;
  }

  
  

}
