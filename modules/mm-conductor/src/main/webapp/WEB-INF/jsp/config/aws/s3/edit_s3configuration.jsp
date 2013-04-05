<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.configuration.AwsConfigurationController"%>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<c:set var="title" value="AWS S3 Configuration" scope="request"/>
<c:set var="activeMenu" value="config" scope="request"/>
<c:set var="activeSubMenu" value="aws_s3" scope="request"/>
<c:set var="urlSubMenu" value="/subNaviConfiguration" scope="request"/>
            
            <div id="content">
                <h1>
                    ${title}
                </h1>
                
                <div>
                    <form:form commandName="conf"  id="myForm" cssClass="decorated">
                    <fieldset>
                        <legend>Settings</legend>
                        <dl>
                            <dt>
                                <label>Access Key:</label>
                            </dt>
                            <dd>
                                <form:input path="accessKey" cssClass="req"/>
                                <form:errors path="accessKey" cssClass="error"/>
                                <img src="/static/images/famfamfam_silk/help.png" title="You will find your access key under 'Security Credenticals' of our account. Go to: https://portal.aws.amazon.com/gp/aws/securityCredentials"/>
                            </dd>
                            <dd class="help">e.g. 'LKUAJAFDEFYMGTKDNBUP'</dd>    
                        </dl>

                        <dl>
                            <dt>
                                <label>Secret Key:</label>
                            </dt>
                            <dd>
                                <form:password path="secretKey" showPassword="false" cssClass="req"/>
                                <form:errors path="secretKey" cssClass="error"/>
                                <img src="/static/images/famfamfam_silk/help.png" title="You will find your secret key under 'Security Credenticals' of our account. Go to: https://portal.aws.amazon.com/gp/aws/securityCredentials"/>
                            </dd>
                            <dd class="help">e.g. 'kirpdotW6lY9zjoddtbjzcD0Oy30jkguw9DTjfOr'<br>Leave blank if you dont't want to change existing secret key.</dd>   
                        </dl>

					    <dl>
					        <dt>
					            <label>Synchronize Media to S3:</label>
					        </dt>
					        <dd>
					           <form:checkbox path="syncToS3" />
                               <img src="/static/images/famfamfam_silk/help.png" title="When you set this option mediamagpie will synchronize all your medias to a bucket on your S3."/>
                            </dd>
					    </dl>

                        <dl class="buttons">
                            <dt>
                                &nbsp;
                            </dt>
                            <dd>
                                <button type="button" onclick="document.location.href='<%=request.getContextPath()+AwsConfigurationController.getBaseRequestMappingUrl()+AwsConfigurationController.URL_S3CONFIG%>'"><span>Cancel</span></button>
                                <button type="submit" class="active"><span>Save</span></button>
                            </dd>   
                        </dl>
                    </fieldset>
                    </form:form>
                </div>
            </div>