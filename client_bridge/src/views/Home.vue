<template>
<div id="main">
    
</div>
</template>

<script>
import { iframeUrl } from "../configs/production.json";
import axios from "axios";

export default {
    name: 'Main',
    data() {
        return {
            iframeUrl: iframeUrl,
            checkNetworkTimekout: undefined
        }
    },

    created()
    {
        // add device ready event
        document.addEventListener("deviceready", () => {
            console.log("device ready, now can call plugin");
        }, false);

        // try to jump
        this.jump();

        // 
        setInterval(() => {
            this.jump();
        }, 500);
    },

    methods: {
        jump() {
            if(/Safari/.test(navigator.userAgent) && !/Chrome/.test(navigator.userAgent))
            {
                window.location.href = iframeUrl;
            }
            else
            {
                window.location.href = iframeUrl;
            }
       },
    }
}
</script>

<style lang="scss" scoped>
#main {
    position: fixed;
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}
</style>
