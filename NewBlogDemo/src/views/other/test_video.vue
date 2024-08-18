<template>
    <div>
  
        <div class="video-out">
            <video class="video" @click="play" id="myVideo" width="100%" height="480px">
                <source src="
                https://vdn-private.vzuu.com/HD/8dcfbe8a-5ca2-11ef-bc72-0eeb7ad9c53f-v8_f2_t1_gsS7W2CC.mp4?auth_key=1723907554-0-0-3da2f7f08e464f8825847b31b13e1b4b&bu=bc22180b&c=avc.8.0&disable_local_cache=1&expiration=1723907554&f=mp4&fs=1860433&pu=1513c7c2&v=private&pp=ChMxNDAxNjIzODY1NzM5NTc5MzkyGGMiC2ZlZWRfY2hvaWNlMhMxMzY5MDA1NjA4NTk5OTA0MjU3PXu830Q%3D&pf=Web&pt=zhihu
                请求方法:
                GET" type="video/mp4">
                Your browser does not support the video tag.
            </video>
        </div>

        <button @click="play">播放</button>
  
    </div>
</template>
  
<script>
export default {
    components: {
    },
    data() {
        return{
        }
    },
    methods:{
        play(){
            // 获取视频元素
            var video = document.getElementById('myVideo');
            // 判断视频是否正在播放
            if (video.paused) {
                // 如果视频处于暂停状态，则播放视频
                video.play();
            } else {
                // 如果视频正在播放，则暂停视频
                video.pause();
            }
        }

    },
    mounted(){
        // 获取视频元素
        var video = document.getElementById('myVideo');
        // 监听视频可以播放的事件
        video.addEventListener('canplay', function() {
        // 当视频可以播放时，自动播放视频
            //video.play();
        });

        video.addEventListener('play', function() {
            console.log('视频正在播放');
        });

        // 监听视频暂停事件
        video.addEventListener('pause', function() {
            console.log('视频已暂停');
        });


    }
}
</script>
  
<style scoped>
.video{
    background-color: black;
}
.video-out{
    height:480px;
    overflow: hidden;
}
</style>