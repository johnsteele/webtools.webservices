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
package org.eclipse.wst.command.internal.env.core.registry;

import org.eclipse.wst.command.internal.env.core.fragment.CommandFragmentFactoryFactory;

/**
 * This interface provides a way to create a CommandFragmentFactoryFactory
 * from an array of ids.
 *
 */
public interface CommandRegistry 
{  
  /**
   * Creates a CommandFragmentFactoryFactory from an array of ids.
   * 
   * @param ids the ids.
   * @return the CommandFragmentFactoryFactory object.
   */
  public CommandFragmentFactoryFactory getFactoryFactory( String[] ids );
}
