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
package org.eclipse.wst.wsdl.ui.internal.asd.properties.sections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.wsdl.WSDLElement;
import org.eclipse.wst.wsdl.ui.internal.actions.AddElementAction;
import org.eclipse.wst.wsdl.ui.internal.adapters.WSDLBaseAdapter;
import org.eclipse.wst.wsdl.ui.internal.asd.ASDEditorCSHelpIds;
import org.eclipse.wst.wsdl.ui.internal.asd.design.editparts.model.AbstractModelCollection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DocumentationSection extends ASDAbstractSection {
	Text docText;

	/**
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory)
	 */
	public void createControls(Composite parent, TabbedPropertySheetWidgetFactory factory)
	{
		super.createControls(parent, factory);
		composite =	getWidgetFactory().createFlatFormComposite(parent);

		docText = getWidgetFactory().createText(composite, "", SWT.MULTI | SWT.NONE | SWT.H_SCROLL | SWT.V_SCROLL); //$NON-NLS-1$
		docText.addListener(SWT.KeyDown, this);
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(100, 0);
		docText.setLayoutData(data);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(docText, ASDEditorCSHelpIds.PROPERTIES_DOCUMENTATION_TAB);
	}

	/*
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
	 */
	/*
	 * TODO: the doHandleEvent() and refresh() methods both use WSDL11 specific
	 * knowledge.  This class (DocumentationSection) should be generic.  In Post 1.5,
	 * we need to add documentation support methods to our facade so we don't need
	 * to know WSDL specific implementation....... 
	 */
	public void refresh()
	{
		super.refresh();
		if (docText.isFocusControl())
		{
			return;
		}
		setListenerEnabled(false);
		docText.setText(""); //$NON-NLS-1$
		if (getModel() != null && getElement() != null)
		{
			Element docNode = getElement().getDocumentationElement();
			if (docNode != null)
			{
				Node textNode = docNode.getFirstChild();
				if (textNode != null)
				{
					String docValue = textNode.getNodeValue();
					if (docValue != null)
					{
						docText.setText(docValue);
					}
				}
			}
		}
		setListenerEnabled(true);
	}

	/*
	 * TODO: the doHandleEvent() and refresh() methods both use WSDL11 specific
	 * knowledge.  This class (DocumentationSection) should be generic.  In Post 1.5,
	 * we need to add documentation support methods to our facade so we don't need
	 * to know WSDL specific implementation....... 
	 */
	public void doHandleEvent(Event event)
	{
		if (event.widget == docText)
		{
			String value = docText.getText();
			if (getModel() != null && getElement() != null)
			{
				Element docNode = getElement().getDocumentationElement();
				if (docNode != null)
				{
					Node textNode = docNode.getFirstChild();
					if (textNode != null)
					{
						textNode.setNodeValue(value);
					}
					else
					{
						if (value.length() > 0)
						{
							Document document = docNode.getOwnerDocument();
							org.w3c.dom.Text newTextNode = document.createTextNode(value);
							docNode.appendChild(newTextNode);
						}
					}
				}
				else
				{
					Element element = getElement().getElement();
					AddElementAction action = new AddElementAction(element, element.getPrefix(), "documentation", element.getFirstChild()); //$NON-NLS-1$
					action.run();
					Element newDocumentation = action.getNewElement();

					Document document = newDocumentation.getOwnerDocument();
					org.w3c.dom.Text newTextNode = document.createTextNode(value);
					newDocumentation.appendChild(newTextNode);
					getElement().setDocumentationElement(newDocumentation);
				}
			}
		}
	}

	private WSDLElement getElement() {
		WSDLElement element = null;
		Object model = getModel();

		if (model instanceof AbstractModelCollection) {
			model = ((AbstractModelCollection) model).getModel();
		}
		
		// TODO: Should we be handling the XML Schema case here as well?
		if (model instanceof WSDLBaseAdapter) {
			Object target = ((WSDLBaseAdapter) model).getTarget();
			if (target instanceof WSDLElement) {
				element = (WSDLElement) target;
			}
		}

		return element;
	}
}