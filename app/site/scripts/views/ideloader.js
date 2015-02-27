/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
 
define(["jquery","underscore","backbone","models/account"],

    function($,_,Backbone,Account){

        var animationFrequency = 500;

        var IDELoader = function(){
            if (Account.isWebsocketEnabled()) {
                this.direction = "next";
                setInterval(_.bind(this.onFrameUpdate,this),animationFrequency);
                Account.waitForTenant(
                    _.bind(this.onTenantReady,this),
                    _.bind(this.onTenantError,this)
                );
            }


        };

        _.extend(IDELoader.prototype, Backbone.Events, {

            onTenantReady : function(d){
                    this.trigger("ready",d);
            },

            onTenantError : function(errors){
                if(errors.length !== 0){
                    this.trigger(
                        "error",
                        errors[0].getErrorDescription()
                    );
                }
            },

            onFrameUpdate : function(){
                var currentFrame = this.getCurrentFrame();
                this.getNextFrame().addClass("visible");
                currentFrame.removeClass("visible");
            },

            getNextFrame : function(){
                var currentFrame = this.getCurrentFrame();

                if(this.direction === "next"){
                    if(currentFrame.next(".loader").length !== 0){
                        return currentFrame.next(".loader");
                    }else{
                        this.direction = "prev";
                        return currentFrame.prev(".loader");
                    }
                }else{
                    if(currentFrame.prev(".loader").length !== 0){
                        return currentFrame.prev(".loader");
                    }else{
                        this.direction = "next";
                        return currentFrame.next(".loader");
                    }
                }
            },

            getCurrentFrame : function(){
                return $(".loader.visible");
            }
        });

        _.extend(IDELoader.prototype, Backbone.View, {
          setLoaderMessage : function() {
                if (Account.getQueryParameterByName("type")==="factory" || Account.getQueryParameterByName("type")==="create"){
                    $(".loader-text").html("Creating your environment");
                }
            }
        });

        return {

            get : function(){
                return new IDELoader();
            },

            IDELoader : IDELoader

        };

    }
);