<template>
  <div>
    <el-cascader
      :options="options"
      ref="cascaderAddr"
      v-model="selectedOptions"
      @change="handleChange"
      >
    </el-cascader>
  </div>
</template>

<script>
let pcas = require('/src/assets/pca-code.json')

export default {
    name: 'receiveAddress',
    props:{
        PselectedOptions:Array
    },
    data() {
        return {
            options: pcas,
            selectedOptions: [],
        }
    },
    methods: {
        // 获取省市区地址级联
        handleChange(e, form, thsAreaCode) {
            thsAreaCode = this.$refs?.['cascaderAddr'].getCheckedNodes()?.[0].pathLabels// 注意2： 获取label值
            console.log(thsAreaCode) // 注意3： 最终结果是个一维数组对象
            console.log(thsAreaCode?.[0] + thsAreaCode?.[1] + thsAreaCode?.[2])
            console.log(this.selectedOptions)
            this.$emit('info',thsAreaCode)
            this.$emit('info_code',this.selectedOptions)
        },
    },
    mounted(){
        this.$store.state.zhezhao_show = false
        this.$store.state.zhezhao_show = false
        if(this.PselectedOptions)this.selectedOptions = JSON.parse(JSON.stringify(this.PselectedOptions))
        else this.selectedOptions = []
    },
    watch:{
        PselectedOptions:function(){
            if(this.PselectedOptions)this.selectedOptions = JSON.parse(JSON.stringify(this.PselectedOptions))
            else this.selectedOptions = []
        }
    }
}
</script>

<style>
.el-cascader-panel {
  max-width: 100vw; /* 限制最大宽度为视口宽度 */
  overflow-x: auto; /* 当需要时出现水平滚动条 */
  overflow-y: visible; /* 允许垂直溢出 */
}


.el-cascader-menu {
  flex: none; /* 使每个菜单项能够独立排列 */
  display: inline-block; /* 级联菜单横向排列 */
  float: none !important; /* 取消可能的浮动效果 */
  height: auto !important; /* 取消可能的绝对高度，允许自适应 */
  min-width: 0 !important; /* 确保菜单宽度可以根据内容缩小 */
}
</style>
