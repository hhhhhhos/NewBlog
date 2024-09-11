<template>
    <div>
      <button @click="playVideo">播放视频</button>
      <iframe
        ref="bilibiliIframe"
        src=""
        allowfullscreen="allowfullscreen"
        width="100%"
        height="500"
        scrolling="no"
        frameborder="0"
        sandbox="allow-top-navigation allow-same-origin allow-forms allow-scripts"
      ></iframe>
    </div>
  </template>
  
<script>
export default {
    methods: {
        playVideo() {
            const iframe = this.$refs.bilibiliIframe;
            iframe.src = '//player.bilibili.com/player.html?aid=60016166&cid=104514776&page=1&danmaku=0&volume=0.5'

            // 假设你的iframe有一个ID为'bilibili-iframe'
            const iframe2 = document.getElementById('bilibiliIframe');

            // 确保iframe已经加载完毕
            iframe2.onload = () => {
                // 设置音量为50%
                const message = {
                    command: 'volume',
                    value: 0.5 // 音量大小，范围是0到1
                };

                // 发送消息给iframe
                iframe2.contentWindow.postMessage(message, '*');
            };

        },

    },
    mounted(){
    }
};
</script>
  
  <style scoped>
  button {
    margin: 20px;
    padding: 10px 20px;
    background-color: #007bff;
    color: white;
    border: none;
    cursor: pointer;
    font-size: 16px;
  }
  </style>