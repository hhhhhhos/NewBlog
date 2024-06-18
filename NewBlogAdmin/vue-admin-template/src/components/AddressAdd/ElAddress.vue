<template>
  <div>
    <el-cascader
      :options="options"
      ref="cascaderAddr"
      v-model="selectedOptions"
      @change="handleChange">
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
