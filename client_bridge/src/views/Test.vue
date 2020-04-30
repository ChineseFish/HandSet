<template>
<div id="main">
    <textarea v-model="text"></textarea>
    <button @click="speak">语音播报cordova</button>
    <button @click="speechGOImmediate">语音播报</button>
    <textarea v-model="identifier"></textarea>
    <textarea v-model="index"></textarea>
    <button @click="setIndentifier">设置汽车车牌cordova</button>
    <button @click="speechGO">设置汽车车牌</button>
    <button @click="speechGODestroy">停止播报</button>
</div>
</template>

<script>
import axios from "axios";

export default {
    name: 'Main',
    data() {
        return {
            text: "",
            identifier: "",
            index: ""
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
            interval.setIndentifier(this.identifier, this.index, function() {
                console.log("setIndentifier success");
            }, function() {
                console.log("setIndentifier failed");
            })
        },

        speechGOImmediate() {
            //
            zsgtzn.speechGOImmediate(this.text);
        },

        speechGO() {
            //
            zsgtzn.speechGO(this.identifier, this.index);
        },

        speechGODestroy() {
            zsgtzn.speechGODestroy();
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
