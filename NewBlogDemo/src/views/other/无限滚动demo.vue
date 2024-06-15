<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div>
    <div id="fuck">
      <div v-for="i in count" :key="i" style="height: 300px;background-color: aquamarine;display: flex;justify-content: center;align-items: center;">
        <h1>{{ i }}</h1>
      </div>
    </div>
    <h5 v-if="loading1">加载中</h5>
    <div style="height: 90px;background-color: bisque;"></div>
  </div>
</template>

<script>
  export default {
    data () {
      return {
        count:5,
        loading1:false
      }
    },
    methods: {
    },
    mounted(){
    this.$store.state.zhezhao_show = false
      var div = document.getElementById('fuck');
      window.addEventListener('scroll', () => {
        var rect = div.getBoundingClientRect();
        console.log("1:"+window.pageYOffset) //当前y轴坐标
        console.log("2:"+rect.bottom) // id元素底部y坐标-当前窗口y坐标 的值
        console.log("3:"+window.innerHeight) // 当前窗口高度
        if(rect.bottom<=window.innerHeight&&!this.loading1){
          console.log("底部已进入视窗!!!")
          this.loading1 = true
          setTimeout(() => {this.count+=5,this.loading1 = false}, 2000);
        }
      })
    }
  }
</script>