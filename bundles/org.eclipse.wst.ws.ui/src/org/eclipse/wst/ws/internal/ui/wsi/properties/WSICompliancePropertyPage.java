/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 * yyyymmdd bug      Email and other contact information
 * -------- -------- -----------------------------------------------------------
 * 20060607   144978 kathy@ca.ibm.com - Kathy Chan
 *******************************************************************************/
package org.eclipse.wst.ws.internal.ui.wsi.properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.wst.ws.internal.plugin.WSPlugin;
import org.eclipse.wst.ws.internal.preferences.PersistentWSIContext;
import org.eclipse.wst.ws.internal.ui.WstWSUIPluginMessages;
import org.eclipse.wst.ws.internal.ui.plugin.WSUIPlugin;


public class WSICompliancePropertyPage extends PropertyPage implements SelectionListener
{
	
	public static final String PAGE_ID= "org.eclipse.wst.ws.internal.ui.wsi.properties.WSICompliancePropertyPage"; //$NON-NLS-1$
	
  
  /*CONTEXT_ID PWSI0001 for the WS-I Preference Page*/
  private String INFOPOP_PWSI_PAGE = WSUIPlugin.ID + ".PWSI0000";
  //

  private Label wsi_ssbp_Label_;
  private Combo wsi_ssbp_Types_;
  
  /*CONTEXT_ID PWSI0004 for the WS-I SSBP type combo box on the WS-I AP Non compliance Preference Page*/
  private String INFOPOP_PWSI_SSBP_COMBO_TYPE = WSUIPlugin.ID + ".PWSI0004";
  /*CONTEXT_ID PWSI0008 for the WS-I AP type combo box on the WS-I AP Non compliance Preference Page*/
  private String INFOPOP_PWSI_AP_COMBO_TYPE = WSUIPlugin.ID + ".PWSI0008";

  private Label wsi_ap_Label_;
  private Combo wsi_ap_Types_;
  private int savedSSBPSetting_ = -1;
  
 /**
   * Creates preference page controls on demand.
   *   @param parent  the parent for the preference page
   */
  protected Control createContents(Composite superparent)
  {
	
    IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
    
    Composite   parent = new Composite( superparent, SWT.NONE );	
    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    parent.setLayout( layout );
    parent.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    parent.setToolTipText(WstWSUIPluginMessages.TOOLTIP_PWSI_PAGE);
    helpSystem.setHelp(parent, INFOPOP_PWSI_PAGE);

    GridLayout gl = new GridLayout();
    gl.numColumns = 2;
    gl.marginHeight = 0;
    gl.marginWidth = 0;

    Composite wsi_Composite = new Composite (parent, SWT.NONE);
    wsi_Composite.setLayout(gl);
    wsi_Composite.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        
    wsi_ap_Label_ = new Label(wsi_Composite, SWT.NONE);
    wsi_ap_Label_.setText(WstWSUIPluginMessages.LABEL_WSI_AP);
    wsi_ap_Label_.setToolTipText(WstWSUIPluginMessages.TOOLTIP_PWSI_AP_LABEL);
    wsi_ap_Types_ = new Combo(wsi_Composite, SWT.DROP_DOWN | SWT.READ_ONLY);
    wsi_ap_Types_.setToolTipText(WstWSUIPluginMessages.TOOLTIP_PWSI_AP_COMBO);
    wsi_ap_Types_.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    helpSystem.setHelp(wsi_ap_Types_,INFOPOP_PWSI_AP_COMBO_TYPE);
    
    wsi_ap_Types_.add(WstWSUIPluginMessages.STOP_NON_WSI);
    wsi_ap_Types_.add(WstWSUIPluginMessages.WARN_NON_WSI);
    wsi_ap_Types_.add(WstWSUIPluginMessages.IGNORE_NON_WSI);
    wsi_ap_Types_.add(WstWSUIPluginMessages.FOLLOW_WSI_PREFERENCE);

    wsi_ap_Types_.addSelectionListener(this);
    
    wsi_ssbp_Label_ = new Label(wsi_Composite, SWT.NONE);
    wsi_ssbp_Label_.setText(WstWSUIPluginMessages.LABEL_WSI_SSBP);
    wsi_ssbp_Label_.setToolTipText(WstWSUIPluginMessages.TOOLTIP_PWSI_SSBP_LABEL);
    wsi_ssbp_Types_ = new Combo(wsi_Composite, SWT.DROP_DOWN | SWT.READ_ONLY);
    wsi_ssbp_Types_.setToolTipText(WstWSUIPluginMessages.TOOLTIP_PWSI_SSBP_COMBO);
    wsi_ssbp_Types_.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    helpSystem.setHelp(wsi_ssbp_Types_,INFOPOP_PWSI_SSBP_COMBO_TYPE);
    
    wsi_ssbp_Types_.add(WstWSUIPluginMessages.STOP_NON_WSI);
    wsi_ssbp_Types_.add(WstWSUIPluginMessages.WARN_NON_WSI);
    wsi_ssbp_Types_.add(WstWSUIPluginMessages.IGNORE_NON_WSI);
    wsi_ssbp_Types_.add(WstWSUIPluginMessages.FOLLOW_WSI_PREFERENCE);

    initializeValues();
    org.eclipse.jface.dialogs.Dialog.applyDialogFont(superparent);    
    return parent;
  }

  /**
   * Does anything necessary because the default button has been pressed.
   */
  protected void performDefaults()
  {
    super.performDefaults();
    initializeDefaults();
  }

  /**
   * Do anything necessary because the OK button has been pressed.
   *  @return whether it is okay to close the preference page
   */
  public boolean performOk()
  {
    storeValues();
    return true;
  }

  protected void performApply()
  {
    performOk();
  }

  /**
   * Initializes states of the controls using default values
   * in the preference store.
   */
  private void initializeDefaults()
  {
    wsi_ssbp_Types_.select(wsi_ssbp_Types_.indexOf(WstWSUIPluginMessages.FOLLOW_WSI_PREFERENCE));
    int apSelection = wsi_ap_Types_.indexOf(WstWSUIPluginMessages.FOLLOW_WSI_PREFERENCE);
    wsi_ap_Types_.select(apSelection);
    savedSSBPSetting_ = -1;  // do not restore saved SSBP setting
    processAPSelection(apSelection);
  }

  /**
   * Initializes states of the controls from the preference store.
   */
  private void initializeValues() {
  	String WSIText = getWSISelection(WSPlugin.getInstance().getWSISSBPContext());
    wsi_ssbp_Types_.select(wsi_ssbp_Types_.indexOf(WSIText));
    
    int apSelection = wsi_ap_Types_.indexOf(getWSISelection(WSPlugin.getInstance().getWSIAPContext()));
    wsi_ap_Types_.select(apSelection);
    savedSSBPSetting_ = -1;  // do not restore saved SSBP setting
    processAPSelection(apSelection);
	}

  private String getWSISelection(PersistentWSIContext context)
  {
    
  	IProject project = (IProject) getElement();
  	String WSIvalue = context.getProjectWSICompliance(project);
	String WSIText = WstWSUIPluginMessages.FOLLOW_WSI_PREFERENCE;
	if (PersistentWSIContext.STOP_NON_WSI.equals(WSIvalue)) {
		WSIText = WstWSUIPluginMessages.STOP_NON_WSI;
	} else if (PersistentWSIContext.WARN_NON_WSI.equals(WSIvalue)) {
		WSIText = WstWSUIPluginMessages.WARN_NON_WSI;
	} else if (PersistentWSIContext.IGNORE_NON_WSI.equals(WSIvalue)) {
		WSIText = WstWSUIPluginMessages.IGNORE_NON_WSI;
	}
	return WSIText;
  }
  
  /**
   * Stores the values of the controls back to the preference store.
   */
  private void storeValues()
  {
  	updateWSIContext(wsi_ssbp_Types_.getSelectionIndex(), WSPlugin.getInstance().getWSISSBPContext());
  	updateWSIContext(wsi_ap_Types_.getSelectionIndex(), WSPlugin.getInstance().getWSIAPContext());
  }
  
  private void updateWSIContext(int selectionIndex, PersistentWSIContext context)
  {    
  	String value = PersistentWSIContext.FOLLOW_WSI_PREFERENCE;
    IProject project = (IProject) getElement();
    
    switch (selectionIndex) {
    	case 0:
    		value = PersistentWSIContext.STOP_NON_WSI;
    		break;
    	case 1:
    		value = PersistentWSIContext.WARN_NON_WSI;
    		break;
    	case 2:
    		value = PersistentWSIContext.IGNORE_NON_WSI;		
    		break;
    	case 3:
    		value = PersistentWSIContext.FOLLOW_WSI_PREFERENCE;		
    		break;
    }
    context.updateProjectWSICompliances(project, value);
  }
  
  public void widgetSelected(SelectionEvent e)
  {
  	
  	processAPSelection( wsi_ap_Types_.getSelectionIndex() );
  	
  }
  
  public void processAPSelection(int selection) {
  	if (selection == 2) { // reset SSBP to default if AP is ignore
  		wsi_ssbp_Types_.setEnabled(true);
  		if (savedSSBPSetting_ != -1)  {
  			// restore saved SSBP setting, if any
  			wsi_ssbp_Types_.select(savedSSBPSetting_);
  			savedSSBPSetting_ = -1;
  		}
  	} else if (selection == 0 || selection == 1) { // set SSBP to follow AP setting if STOP or WARN chosen
  		if (savedSSBPSetting_ == -1)  {  // SSBP setting not saved
  			savedSSBPSetting_ = wsi_ssbp_Types_.getSelectionIndex();
  		}
  		wsi_ssbp_Types_.select(selection);
  		wsi_ssbp_Types_.setEnabled(false);
  		
  	}
  }

  public void widgetDefaultSelected(SelectionEvent e) {
	
  }
}

