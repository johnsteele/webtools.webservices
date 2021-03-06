/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.ws.internal.uddiregistry.widgets;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.command.internal.env.core.common.Condition;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;


public class PrivateUDDISelectionWidgetConditionCommand extends AbstractDataModelOperation implements Condition
{
  private boolean condition;

  public PrivateUDDISelectionWidgetConditionCommand()
  {
    condition = true;
  }
  
  public IStatus execute( IProgressMonitor monitor, IAdaptable adaptable )
  {
    condition = !condition;
    return Status.OK_STATUS;
  }
  
  public IStatus undo( IProgressMonitor monitor, IAdaptable adaptable )
  {
    condition = !condition;
    return Status.OK_STATUS;
  }
  
  public boolean evaluate()
  {
    return condition;
  }
}
