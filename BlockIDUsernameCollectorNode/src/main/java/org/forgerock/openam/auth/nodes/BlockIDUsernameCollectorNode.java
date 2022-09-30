/*
 *
 * Copyright 1Kosmos Inc
 */


package org.forgerock.openam.auth.nodes;

import static org.forgerock.openam.auth.node.api.SharedStateConstants.USERNAME;

import java.util.Enumeration;
import java.util.List;
import java.io.UnsupportedEncodingException;

import javax.inject.Inject;
import javax.mail.internet.MimeUtility;

import org.forgerock.openam.annotations.sm.Attribute;
import org.forgerock.openam.auth.node.api.AbstractDecisionNode;
import org.forgerock.openam.auth.node.api.Action;
import org.forgerock.openam.auth.node.api.Node;
import org.forgerock.openam.auth.node.api.NodeProcessException;
import org.forgerock.openam.auth.node.api.TreeContext;
import org.forgerock.openam.utils.StringUtils;
import org.forgerock.openam.identity.idm.IdentityUtils;
import org.forgerock.openam.core.realms.Realm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.forgerock.json.JsonValue;


import com.google.inject.assistedinject.Assisted;
import com.sun.identity.idm.IdType;


/**
 * A node that checks to see if BlockID profile is retrived via Universal Web Login mechanism 
 * and inject the username into ForgeRock Access Manager headers for passwordless login.
 */
@Node.Metadata(outcomeProvider  = AbstractDecisionNode.OutcomeProvider.class,
               configClass      = BlockIDUsernameCollectorNode.Config.class)
public class BlockIDUsernameCollectorNode extends AbstractDecisionNode {

    private final Logger logger = LoggerFactory.getLogger(BlockIDUsernameCollectorNode.class);
    private final Config config;
    private final IdentityUtils identityUtils;
    private final Realm realm;

    /**
     * Configuration for the BlockIDUsernameCollectorNode.
     */
    public static interface Config {
        /**
         * The header name for zero-page login that will contain the identity's username.
         */
        @Attribute(order = 100)
        default String usernameHeader() {
            return "X-OpenAM-Username";
        }
    }


    /**
     *
     * @param config The service config.
     * @param realm The realm the node is in.
     * @throws NodeProcessException If the configuration was not valid.
     */
    @Inject
    public BlockIDUsernameCollectorNode(@Assisted Config config, @Assisted Realm realm, IdentityUtils identityUtils) throws NodeProcessException {
        this.config = config;
        this.realm = realm;
        this.identityUtils = identityUtils;
    }

    @Override
    public Action process(TreeContext context) throws NodeProcessException {
    	logger.debug("BlockIDUsernameCollectorNode Started");
    	boolean hasBlockID = false;
        String BlockIDuserid = context.request.servletRequest.getParameter("BlockIDuserid").toString();
        logger.debug("BlockIDUsernameCollectorNode Input parameter:" + BlockIDuserid);
        if(BlockIDuserid!=null ) {
        	hasBlockID = true;
        }
        Enumeration<String> params =  context.request.servletRequest.getParameterNames(); 
        while(params.hasMoreElements()){
         String paramName = params.nextElement();
         logger.debug("Parameter Name - "+paramName+", Value - "+ context.request.servletRequest.getParameter(paramName));
        }        
        if(hasBlockID) {
        	String userName = BlockIDuserid;
        	logger.debug("Username obtained by BlockID Auth Node. Setting ForgeRock AM username:" + userName );        	
        	JsonValue sharedState = context.sharedState.copy();
            JsonValue transientState = context.transientState.copy();
            updateStateIfPresentForBlockID(context, true, config.usernameHeader(), USERNAME, sharedState,userName);
            String realm = context.sharedState.get("realm").asString();
            return goTo(true)
                      .withUniversalId(identityUtils.getUniversalId(userName, realm, IdType.USER))
                      .replaceSharedState(sharedState).replaceTransientState(transientState).build();
        }
        return goTo(true).build();
    }

    private void updateStateIfPresentForBlockID(TreeContext context, boolean hasValue, String headerName, String stateKey,
            JsonValue state, String BlockIDuserid) throws NodeProcessException {
        if (hasValue) {
            List<String> values = context.request.headers.get(headerName);
            String value = BlockIDuserid;
            try {
                if (StringUtils.isNotEmpty(value)) {
                    value = MimeUtility.decodeText(value);
                }
            } catch (UnsupportedEncodingException e) {
                logger.error("Could not decode username or password header");
            }
            state.put(stateKey, value);
        }
    }
}
