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
package org.eclipse.wst.command.internal.env.core.fragment;

public interface CommandFragmentFactoryFactory 
{
  /**
   * The framework calls this method to get the CommandFragmentFactory
   * for a particular extension.
   * 
   * @return Creates a CommandFragmentFactory.
   */
  public CommandFragmentFactory create();  
}
