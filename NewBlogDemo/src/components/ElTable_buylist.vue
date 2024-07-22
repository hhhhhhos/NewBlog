<template>
  <div>

    <div v-if="!this.$store.state.IsMobile">
      <el-table
        ref="myTable"
        v-loading=IsTableLoading
        :data="tableData"
        style="width: 100%"
        @selection-change="handleSelectionChange"
        id="fuck"
        >
        <el-table-column
          type="selection"
          width="55">
        </el-table-column>
        <el-table-column
          v-for="(column, index) in this.columns"
          :key="index"
          :prop="column.prop"
          :label="column.label"
          :width="column.width">
          <template slot-scope="scope" >
            <div v-if="tableData?.[scope.$index]?.['product']?.num!==0">
              <div v-if="column.label=='创建时间'">{{ tableData?.[scope.$index]?.['buylist'][column.prop].replace(/T/g, ' ') }}</div>
              <div v-else-if="column.label=='商品图'"><img loading="lazy"  :src="require(`@/assets/${tableData?.[scope.$index]?.['product']?.[column.prop]?tableData?.[scope.$index]?.['product']?.[column.prop]:'noproduct'}.webp`)" style="height:100px;width: 100px;object-fit:cover;"></div>
              <div v-else-if="column.label=='购买数量'" style="display: flex;" class="myc1">
                  <el-button icon="el-icon-plus" circle size="mini" @click="tableData[scope.$index]['product'].num>tableData[scope.$index]['buylist'][column.prop] ? tableData[scope.$index]['buylist'][column.prop]++:overnum(tableData[scope.$index]['buylist'][column.prop])"></el-button>
                    <div style="width: 25px;text-align: center;margin-top: 2px;">{{ tableData[scope.$index].buylist[column.prop] }}</div>
                  <el-button icon="el-icon-minus" circle size="mini" @click="tableData[scope.$index]['buylist'][column.prop]>1?tableData[scope.$index]['buylist'][column.prop]--:tableData[scope.$index]['buylist'][column.prop]"></el-button>
              </div>
              <div v-else-if="column.label=='操作'">
                <el-button type="danger" icon="el-icon-delete" circle @click="confirmtodelete(tableData[scope.$index].product.name,tableData[scope.$index].buylist.id)"></el-button>
              </div>
              <div v-else-if="column.label=='ID'">{{ tableData?.[scope.$index]?.['buylist']?.[column.prop] }}</div>
              <div v-else-if="column.label=='价格'" style="color: rgba(255,80,0);font-weight: bolder;font-size:medium;">{{ tableData?.[scope.$index]?.['product'][column.prop] }}</div>
              <div v-else>{{ tableData?.[scope.$index]?.['product'][column.prop] }}</div>
            </div>
            <div v-else>
              <div v-if="column.label=='创建时间'">{{ tableData?.[scope.$index]?.['buylist'][column.prop].replace(/T/g, ' ') }}</div>
              <div v-else-if="column.label=='商品图'"><img loading="lazy"  :src="require(`@/assets/${tableData?.[scope.$index]?.['product']?.[column.prop]}.webp`)" style="height:100px;width: 100px;object-fit:cover;"></div>
              <div v-else-if="column.label=='购买数量'" style="display: flex;" class="myc1">
                  <p style="color: darkgray;">商品卖完啦</p>
              </div>
              <div v-else-if="column.label=='操作'">
                <el-button type="danger" icon="el-icon-delete" circle @click="confirmtodelete(tableData[scope.$index].product.name,tableData[scope.$index].buylist.id)"></el-button>
              </div>
              <div v-else-if="column.label=='ID'">{{ tableData?.[scope.$index]?.['buylist'][column.prop] }}</div>
              <div v-else-if="column.label=='价格'" style="color: rgba(255,80,0);font-weight: bolder;font-size:medium;">{{ tableData?.[scope.$index]?.['product'][column.prop] }}</div>
              <div v-else>{{ tableData?.[scope.$index]?.['product']?.[column.prop] }}</div>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <h5 v-if="loading1">加载中</h5>
    </div>


    
    <div v-else>
      <el-table
        ref="myTable"
        v-loading=IsTableLoading
        :data="tableData"
        style="width: 95%;margin: 10px auto;"
        @selection-change="handleSelectionChange"
        id="fuck"
        >
        <el-table-column
          type="selection"
          width="35">
        </el-table-column>
        <el-table-column
          v-for="(column, index) in this.columns"
          :key="index"
          :prop="column.prop"
          :label="column.label"
          :width="column.width"
          :align="column.align">
          <template slot-scope="scope" >
            <!-- 库存不为0 -->
            <div v-if="tableData?.[scope.$index]?.['product']?.num!==0">
              <div v-if="column.label=='创建时间'">{{ tableData?.[scope.$index]?.['buylist']?.[column.prop]?.replace(/T/g, ' ') }}</div>
              <div v-else-if="column.label=='商品图'"><img @click="$openProduct(tableData?.[scope.$index]?.['buylist']?.product_id)" loading="lazy"  :src="require(`@/assets/${tableData?.[scope.$index]?.['product']?.[column.prop]?tableData?.[scope.$index]?.['product']?.[column.prop]:'noproduct'}.webp`)" style="height:100px;width: 100px;object-fit:cover;"></div>
              <div v-else-if="column.label=='信息'">
                <div style="text-align: left;margin: 0 0px 10px 2vw;">{{ tableData[scope.$index]['product'].name }}</div>
                <div style="text-align: left;margin: 0 0px 10px 2vw;color:red;">¥<span style="color:red;margin-left: 5px;">{{ tableData[scope.$index]['product'].price }}</span></div>
                <div style="display: flex;justify-content:start;margin: 0 0 0 2vw;">
                  <el-button icon="el-icon-plus" circle size="mini" @click="tableData[scope.$index]['product'].num>tableData[scope.$index]['buylist'][column.prop] ? tableData[scope.$index]['buylist'][column.prop]++:overnum(tableData[scope.$index]['buylist'][column.prop])"></el-button>
                    <div style="width: 25px;text-align: center;margin-top: 2px;">{{ tableData[scope.$index].buylist[column.prop] }}</div>
                  <el-button icon="el-icon-minus" circle size="mini" @click="tableData[scope.$index]['buylist'][column.prop]>1?tableData[scope.$index]['buylist'][column.prop]--:tableData[scope.$index]['buylist'][column.prop]"></el-button>
                </div>
                
              </div>
              <div v-else-if="column.label=='操作'">
                <el-button type="danger" icon="el-icon-delete" circle @click="confirmtodelete(tableData[scope.$index].product.name,tableData[scope.$index].buylist.id)"></el-button>
              </div>
              <div v-else-if="column.label=='ID'">{{ tableData?.[scope.$index]?.['buylist']?.[column.prop] }}</div>
              <div v-else-if="column.label=='价格'" style="color: rgba(255,80,0);font-weight: bolder;font-size:medium;">{{ tableData?.[scope.$index]?.['product'][column.prop] }}</div>
              <div v-else>{{ tableData?.[scope.$index]?.['product'][column.prop] }}</div>
            </div>
            <!-- 库存为0 -->
            <div v-else>
              <div v-if="column.label=='创建时间'">{{ tableData?.[scope.$index]?.['buylist'][column.prop].replace(/T/g, ' ') }}</div>
              <div v-else-if="column.label=='商品图'"><img loading="lazy"  :src="require(`@/assets/${tableData?.[scope.$index]?.['product']?.[column.prop]}.webp`)" style="height:100px;width: 100px;object-fit:cover;"></div>
              <div v-else-if="column.label=='信息'">
                <div style="text-align: left;margin: 0 0px 10px 2vw;">{{ tableData[scope.$index]['product'].name }}</div>
                <div style="text-align: left;margin: 0 0px 10px 2vw;color:red;">¥<span style="color:red;margin-left: 5px;">{{ tableData[scope.$index]['product'].price }}</span></div>
                <div style="display: flex;justify-content:start;margin: 0 0 0 2vw;">
                    <p style="color: darkgray;margin: 0;padding: 0;">商品卖完啦</p>
                </div>
                
              </div>
              <div v-else-if="column.label=='操作'">
                <el-button type="danger" icon="el-icon-delete" circle @click="confirmtodelete(tableData[scope.$index].product.name,tableData[scope.$index].buylist.id)"></el-button>
              </div>
              <div v-else-if="column.label=='ID'">{{ tableData?.[scope.$index]?.['buylist'][column.prop] }}</div>
              <div v-else-if="column.label=='价格'" style="color: rgba(255,80,0);font-weight: bolder;font-size:medium;">{{ tableData?.[scope.$index]?.['product'][column.prop] }}</div>
              <div v-else>{{ tableData?.[scope.$index]?.['product']?.[column.prop] }}</div>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <h5 v-if="loading1">加载中</h5>
      <h5 v-else style="background-color:rgb(243, 243, 244);color: #00000060;border:none;">没有更多了。。</h5>
    </div>

  </div>
</template>

<script>
import axios from '@/utils';

export default {
    props: {
        columns:Array,
        geturl:String,
        Datas:Array
    },
    data() {
        return{
            tableData:[],
            currentPage:1,
            page:{
                "total": null,
                "size": null,
                "current": null,
                "pages":null
            },
            PageSize:10,
            IsTableLoading:true,
            val:[],
            func:null,
            loading1:false
        }
    },
    methods:{
    // 拿表
        async gettable(){
            var result = false
            await axios.get(this.geturl,{
                params: {
                    currentPage: this.currentPage,
                    PageSize: this.PageSize
                }
            }).then(response=>{
                if(response.data.code===0)this.$message.error(response.data.msg)
                else {
                    this.tableData = response.data.data.records
                    this.page.total = response.data.data.total
                    this.page.size = response.data.data.size
                    this.page.current = response.data.data.current
                    this.page.pages = response.data.data.pages
                    this.init_selection() // 初始化商品选中
                    this.$nextTick(() => { // dom之后加载
                        this.func(); // 判断有无更多数据
                    });
                    this.IsTableLoading = false
                    result = true
                }
            }).catch(error=>{
                this.$message.error(error.data.msg)
                console.log(error)
            })
            return result
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
        },
        // 表格列选择
        handleSelectionChange(val){
            console.log(val)
            this.val = val
            // 遍历tableData数组，更新每个元素的is_selected属性
            this.tableData.forEach((dataItem) => {
                // 检查当前元素是否在选中的行数组`val`中
                // 这里假设每个元素有唯一标识符，如id
                dataItem.buylist.is_selected = val.some(selectedItem => selectedItem.buylist.id === dataItem.buylist.id)
            });
            console.log(this.tableData)
            this.$emit('SelectRow',val)
        },
        // 更新表格
        updatetable(){
            axios.post('/buylist/update',this.tableData)
                .then(response=>{
                    if(response.data.code)this.$message.success(response.data.data)
                    else this.$message.error(response.data.msg)
                    console.log(response)
                }).catch(error=>{
                    this.$message.error(error.data.msg);
                    console.log(error)
                })
        },
        // 点击删除
        confirmtodelete(str,id){
            this.$confirm('确定删除'+str+"吗", '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.deletebyid(id)
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消删除'
                })
            });
        },
        // 根据id删除 请求范例
        deletebyid(id){
            axios.delete('/buylist/deletebyid',{params: {D_id:id}})
                .then(response=>{
                    if(response.data.code){
                        this.$message.success(response.data.data)
                        setTimeout(() => {window.location.reload()}, 500);
                    }
                    else this.$message.error(response.data.msg)
                    console.log(response)
                }).catch(error=>{
                    this.$message.error(error.data.msg);
                    console.log(error)
                })
        },
        // 初始化购物车选中 在axios请求购物车时调用
        init_selection() {
            if (this.tableData.length > 0) {
                this.tableData.forEach(RowData => {
                    if (RowData.buylist.is_selected) {
                        this.$nextTick(() => {
                            this.$refs.myTable.toggleRowSelection(RowData, true); // 第二个参数确保行被选中
                        });
                    }
                });
            }
        },
        // 超库存
        overnum(val){
            this.$message.error("商品库存不足")
            return val
        }
    },
    created(){
        if(!this.Datas)this.gettable()
        else {
            this.IsTableLoading = false
            this.tableData = JSON.parse(JSON.stringify(this.Datas))
        }
    },
    // 页面关闭或者刷新不会触发beforeDestroy
    beforeDestroy(){
        this.updatetable()
        window.removeEventListener('scroll',this.func)
    },
    mounted() {
        this.$store.state.zhezhao_show = false
        // 刷新或关闭网页触发
        window.onbeforeunload = () => {
            this.updatetable();
        } 
        // 无限滚动功能监听
        this.func = async () => {
            var div = document.getElementById('fuck');
            var rect = div.getBoundingClientRect();
            console.log("1:"+window.pageYOffset) //当前y轴坐标
            console.log("2:"+rect.bottom) // id元素底部y坐标-当前窗口y坐标 的值
            console.log("3:"+window.innerHeight) // 当前窗口高度
            if(rect.bottom<=window.innerHeight&&!this.loading1){
                console.log("底部已进入视窗!!!")
                // 如果还有数据
                if(this.page.total>this.page.size * this.page.current){
                    this.loading1 = true
                    // 获取新表格前更新数据
                    this.updatetable()
                    setTimeout(async() => {
                        var oldtableData = JSON.parse(JSON.stringify(this.tableData))
                        this.PageSize +=5
                        //var oldScrollPosition = window.pageYOffset
                        //console.log("oldScrollPosition:"+oldScrollPosition)
                        var result = await this.gettable()
                        if(result){
                            this.tableData = oldtableData.concat(this.tableData)
                        }
                        //setTimeout(() => {window.scrollTo(0, oldScrollPosition),this.loading1 = false}, 0);
                        this.loading1 = false
                    }, 1000);// 箭头函数，可以保留上下文this定义域
                }else{
                    return
                }
          
            }
        }
        window.addEventListener('scroll', this.func)

    },
    // 深层监听器
    watch:{
    // 向父组件更新总金额（商品数量变化时）
        tableData:{
            deep: true,
            handler:function(){
                for(let i=0;i<this.val.length;i++){
                    for(let j=0;j<this.tableData.length;j++){
                        if(this.val[i].id===this.tableData[j].id){
                            this.val[i].product_num = this.tableData[j].product_num;
                            break;
                        }
                    }
                }
                this.$emit('SelectRow',this.val)
                //console.log("watch!")
            }
        }
    
    }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.myc1 .el-button:focus {
  background-color: white;
  color: black;
  border-color: #DCDFE6;
}
.myc1 .el-button:hover{
  background-color: white;
}

</style>

<style>
.rowName{
  margin-bottom: 100px; /* 对于单元格内部的每个内容增加下外边距 */
  background-color: aqua;
}

</style>
