<template>
<div id="main">
    <textarea v-model="text"></textarea>
    <button @click="speak">语音播报</button>
    <textarea v-model="identifier"></textarea>
    <button @click="setIndentifier">设置汽车车牌</button>
</div>
</template>

<script>
import axios from "axios";

export default {
    name: 'Main',
    data() {
        return {
            text: "",
            identifier: ""
        }
    },

    created()
    {
        // add device ready event
        document.addEventListener("deviceready", () => {

        }, false);
    },

    methods: {
        speak() {
            //
            tts.textToSpeech(this.text, function () {
                console.log("Success");
            }, function (reason) {
                console.log("Failed: " + reason);
            });
        },

        setIndentifier() {
             //
            interval.setIndentifier(this.identifier, function() {
                console.log("setIndentifier success");
            }, function() {
                console.log("setIndentifier failed");
            })
        }
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
