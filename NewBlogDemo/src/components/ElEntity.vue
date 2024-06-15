<template>
  <div>
    <el-table
      v-loading=IsTableLoading
      :data="tableData"
      style="width: 100%">
      <el-table-column
        v-for="(column, index) in this.columns"
        :key="index"
        :prop="column.prop"
        :label="column.label"
        :width="column.width">
      </el-table-column>
    </el-table>
    <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[5, 10, 15, 20]"
        :page-size=PageSize
        layout="total, sizes, prev, pager, next, jumper"
        :total="TotalPage">
      </el-pagination>
    </div>
</template>

<script>
import axios from '@/utils';

export default {
  props: {
    columns:Array,
    geturl:String
  },
  data() {
    return{
      tableData:[],
      currentPage:1,
      TotalPage:null,
      PageSize:5,
      IsTableLoading:true
    }
  },
  methods:{
    // 拿表
    gettable(){
      axios.get(this.geturl,{
        params: {
          currentPage: this.currentPage,
          PageSize: this.PageSize
        }
      }).then(response=>{
        this.tableData = response.data.records
        this.TotalPage = response.data.total
        this.IsTableLoading = false
        console.log(response)
      }).catch(error=>{
        console.log(error)
      })
    },
    // 页容量变化
    handleSizeChange(val) {
      this.PageSize = val
      this.gettable()
    },
    // 切页
    handleCurrentChange(val) {
      this.currentPage = val
      this.gettable()
    }
  },
  created(){
    this.gettable()
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>
