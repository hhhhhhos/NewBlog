<template>
  <div>
    <div style="margin: 50px 100px;" class="">
      <ElTable1 :columns="columns1" :geturl="geturl"/>  
    </div>
  </div>
</template>

<script>
import ElTable1 from '/src/components/ElTable1'

export default {
  components: {
    ElTable1
  },
  data() {
    return{
      columns1 : [
        { prop: 'id', label: 'ID', width: '180' },
        { prop: 'name', label: '姓名', width: '180' },
        { prop: 'age', label: '年龄', width: '180' },
        { prop: 'sex', label: '姓别', width: '180' },
        { prop: 'address', label: '地址' },
        { prop: 'phone', label: '电话', width: '180' },
        { prop: 'create_time', label: '创建时间', width: '180' }
      ],
      geturl:'/user/page'
    }
  }
}
</script>

<style scoped>

</style>
