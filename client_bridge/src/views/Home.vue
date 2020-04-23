<template>
<div id="main">
    <h1>加载中请耐心等待一会儿</h1>
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
            checkNetworkTimekout: undefined,
        }
    },

    created()
    {
        // add device ready event
        document.addEventListener("deviceready", () => {
            
            //
            tts.textToSpeech("你好呀", function () {
                alert("Success");
            }, function (reason) {
                alert("Failed: " + reason);
            });

        }, false);

        // // try to jump
        // this.jump();

        // // 
        // setInterval(() => {
        //     this.jump();
        // }, 500);
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
    justify-content: center;
    align-items: center;
}
</style>
