<%
/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
%>
<%@ page contentType="text/html; charset=UTF-8" import="org.eclipse.wst.ws.internal.explorer.platform.wsdl.perspective.*,
                                                        org.eclipse.wst.ws.internal.explorer.platform.wsdl.datamodel.*,
                                                        org.eclipse.wst.ws.internal.explorer.platform.wsdl.actions.*,
                                                        org.eclipse.wst.ws.internal.explorer.platform.wsdl.constants.WSDLActionInputs,
                                                        org.eclipse.wst.ws.internal.explorer.platform.wsdl.transport.HTTPException,
                                                        org.eclipse.wst.ws.internal.explorer.platform.wsdl.transport.HTTPTransport,
                                                        org.eclipse.wst.ws.internal.explorer.platform.perspective.*,
                                                        org.eclipse.wst.ws.internal.explorer.platform.constants.*,
                                                        sun.misc.BASE64Decoder,
                                                        javax.servlet.http.HttpServletResponse,
                                                        javax.wsdl.*"%>

<jsp:useBean id="controller" class="org.eclipse.wst.ws.internal.explorer.platform.perspective.Controller" scope="session"/>
<%
// Prepare the action.
InvokeWSDLSOAPOperationSourceAction action = new InvokeWSDLSOAPOperationSourceAction(controller);

// Load the parameters for the action from the servlet request.
boolean inputsValid = action.populatePropertyTable(request);
  
if (action.wasSaveAsSelected())
{
  // write to file system
  response.setContentType("application/octet-stream");
  response.setHeader("Content-Disposition","attachment;filename="+action.getDefaultSaveAsFileName());
  action.writeSourceContent(response.getOutputStream());    
}
else
{
  if (!inputsValid)
  {
  %>
    <jsp:include page="/wsdl/scripts/wsdlpanes.jsp" flush="true"/>
    <html>
      <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script language="javascript" src="<%=response.encodeURL(controller.getPathWithContext("scripts/browserdetect.js"))%>"></script>
      </head>
      <body dir="<%=org.eclipse.wst.ws.internal.explorer.platform.util.DirUtils.getDir()%>">
        <script language="javascript">
          wsdlPropertiesContent.location = "<%=response.encodeURL(controller.getPathWithContext("wsdl/wsdl_properties_content.jsp"))%>";
          wsdlStatusContent.location = "<%=response.encodeURL(controller.getPathWithContext("wsdl/wsdl_status_content.jsp"))%>";
        </script>
      </body>
    </html>
  <%
  }
  else
  {
    if (action.wasNewFileSelected())
    {     
  %>
      <jsp:include page="/wsdl/scripts/wsdlpanes.jsp" flush="true"/>
      <html>
        <head>
          <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
          <script language="javascript" src="<%=response.encodeURL(controller.getPathWithContext("scripts/browserdetect.js"))%>"></script>
        </head>
        <body dir="<%=org.eclipse.wst.ws.internal.explorer.platform.util.DirUtils.getDir()%>">
          <script language="javascript">
            wsdlPropertiesContent.location = "<%=response.encodeURL(controller.getPathWithContext("wsdl/wsdl_properties_content.jsp"))%>";
            wsdlStatusContent.location = "<%=response.encodeURL(controller.getPathWithContext("wsdl/wsdl_status_content.jsp"))%>";
          </script>
        </body>
      </html>
    <%    
    }
    else
    {
      String httpBasicAuthData = request.getHeader(HTTPTransport.HTTP_HEADER_AUTH);
      if (httpBasicAuthData != null && httpBasicAuthData.length() > 0)
      {
        int basicIndex = httpBasicAuthData.indexOf(HTTPTransport.BASIC);
        if (basicIndex != -1)
        {
          BASE64Decoder decoder = new BASE64Decoder();
          httpBasicAuthData = new String(decoder.decodeBuffer(httpBasicAuthData.substring(basicIndex + HTTPTransport.BASIC.length() + 1)));
          int colonIndex = httpBasicAuthData.indexOf(HTTPTransport.COLON);
          if (colonIndex != -1)
          {
            action.addProperty(WSDLActionInputs.HTTP_BASIC_AUTH_USERNAME, httpBasicAuthData.substring(0, colonIndex));
            action.addProperty(WSDLActionInputs.HTTP_BASIC_AUTH_PASSWORD, httpBasicAuthData.substring(colonIndex + 1, httpBasicAuthData.length()));
          }
        }
      }
      try
      {
        // Run the action and obtain the return code (fail/success).
        boolean actionResult = action.execute();
    %>
        <jsp:include page="/wsdl/scripts/wsdlpanes.jsp" flush="true"/>
        <html>
          <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <script language="javascript" src="<%=response.encodeURL(controller.getPathWithContext("scripts/browserdetect.js"))%>"></script>
          </head>
          <body dir="<%=org.eclipse.wst.ws.internal.explorer.platform.util.DirUtils.getDir()%>">
            <script language="javascript">
              wsdlPropertiesContainer.location = "<%=response.encodeURL(controller.getPathWithContext("wsdl/wsdl_properties_container.jsp"))%>";
              <%
              if (actionResult)
              {
              %>
                wsdlStatusContent.location = "<%=response.encodeURL(controller.getPathWithContext("wsdl/wsdl_result_content.jsp"))%>";
              <%
              }
              else
              {
              %>
                wsdlStatusContent.location = "<%=response.encodeURL(controller.getPathWithContext("wsdl/wsdl_status_content.jsp"))%>";
              <%
              }
              %>
            </script>
          </body>
        </html>
<%
      }
      catch (HTTPException httpe)
      {
        int code = httpe.getStatusCode();
        if (code == HttpServletResponse.SC_UNAUTHORIZED)
        {
          response.setStatus(code);
          response.setHeader(HTTPTransport.HTTP_HEADER_CONTENT_LENGTH, String.valueOf(0));
          String wwwAuthData = httpe.getHeader(HTTPTransport.HTTP_HEADER_WWW_AUTH.toLowerCase());
          if (wwwAuthData == null)
            wwwAuthData = httpe.getHeader(HTTPTransport.HTTP_HEADER_WWW_AUTH);
          if (wwwAuthData == null)
            wwwAuthData = HTTPTransport.BASIC;
          response.setHeader(HTTPTransport.HTTP_HEADER_WWW_AUTH, wwwAuthData);
        }
        else
        {
          WSDLPerspective wsdlPerspective = controller.getWSDLPerspective();
          MessageQueue messageQueue = wsdlPerspective.getMessageQueue();
          messageQueue.addMessage(controller.getMessage("MSG_ERROR_UNEXPECTED"));
          messageQueue.addMessage(String.valueOf(code));
          messageQueue.addMessage(httpe.getStatusMessage());
          %>
          <jsp:include page="/wsdl/scripts/wsdlpanes.jsp" flush="true"/>
          <html>
            <head>
              <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
              <script language="javascript" src="<%=response.encodeURL(controller.getPathWithContext("scripts/browserdetect.js"))%>"></script>
            </head>
            <body dir="<%=org.eclipse.wst.ws.internal.explorer.platform.util.DirUtils.getDir()%>">
              <script language="javascript">
                wsdlPropertiesContainer.location = "<%=response.encodeURL(controller.getPathWithContext("wsdl/wsdl_properties_container.jsp"))%>";
                wsdlStatusContent.location = "<%=response.encodeURL(controller.getPathWithContext("wsdl/wsdl_status_content.jsp"))%>";
              </script>
            </body>
          </html>
          <%
        }
      }
    }
  }
}
%>