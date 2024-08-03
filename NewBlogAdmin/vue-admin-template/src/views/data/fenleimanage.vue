<template>
    <div class="fenlei">

        <el-button type="primary" @click="add_re(),dialogtitle='新增',dialogVisible = true">新增分类</el-button>

        <el-table
            :data="tableData"
            style="width: 100%;margin: 20px 0;">
            <el-table-column
                prop="key"
                align="center"
                label="分类数字"
                width="80">
            </el-table-column>
            <el-table-column
                align="center"
                prop="color"
                label="分类颜色"
                width="80">
            </el-table-column>
            <el-table-column
                align="center"
                prop="value"
                label="分类名称">
            </el-table-column>
            <el-table-column
                width="280"
                align="center" 
                label="操作">
                <template slot-scope="scope">
                    <el-button
                    size="mini"
                    @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                    <el-button
                    size="mini"
                    type="danger"
                    @click="handleDelete(scope.$index, scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>


        <!-- 增改分类弹窗 -->
        <el-dialog
        :title=dialogtitle
        :visible.sync="dialogVisible"
        width="80%"
        >

            <el-form label-width="100px">
                
                <el-form-item label="分类数字">
                    <el-input style="width: 300px;" v-model="new_index"></el-input>
                    <p style="margin: 0;">最好数字不然可能报错</p>
                </el-form-item>

                <el-form-item label="数字类名">
                    <el-input style="width: 300px;" v-model="new_item"></el-input>
                </el-form-item>

                <el-form-item label="类名颜色">
                    <el-input style="width: 300px;" v-model="new_color"></el-input>
                </el-form-item>

            </el-form>

            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="addorupdate">确 定</el-button>
            </span>
        </el-dialog>


    </div>
</template>

<script>
import axios from '@/utils/axios';

export default {
    data() {
        return{
            tableData:[],
            dataResult:{
                home_visitors:null,
                back_visitors:null,
                comment_total_num:null,
                product_total_num:null,
                en_list:null,
                cn_list:null,
                co_list:null,
                fenlei_map:null,
                biaoqian_map:null,
            },
            new_index:null,
            new_item:null,
            new_color:null,
            dialogVisible:false,
            dialogtitle:"",
            row:null,
        }
    },
    methods:{
        add_re(){
            this.new_item='',
            this.new_index='',
            this.new_color=''
        },
        handleDelete(index, row){
            delete this.dataResult.fenlei_map[row.key]
            this.updateall()
        },
        handleEdit(index, row){
            this.row=row
            this.new_item=row.value,
            this.new_index=row.key,
            this.new_color=row.color
            this.dialogtitle='修改'
            this.dialogVisible = true
        },
        addorupdate(){
            if(!this.new_index || !this.new_item) return this.$message.error("为空")
            if(this.dialogtitle==='新增' && this.dataResult.fenlei_map[this.new_index]) return this.$message.error("index已存在，小心覆盖")
            if(this.dialogtitle==='新增'){
                this.dataResult.fenlei_map[this.new_index] = this.new_item
                this.dataResult.fenlei_color_map[this.new_index] = this.new_color
            }else{
                delete this.dataResult.fenlei_map[this.row.key]
                this.dataResult.fenlei_map[this.new_index] = this.new_item

                delete this.dataResult.fenlei_color_map[this.row.key]
                this.dataResult.fenlei_color_map[this.new_index] = this.new_color
            }
            this.updateall()
        },
        getall(){
            axios.get('/data-result/all')
                .then(response=>{
                    console.log(response.data)
                    this.dataResult = response.data
                    var new_tableData = []
                    for (let [key, value] of Object.entries(this.dataResult.fenlei_map)) {
                        new_tableData.push({ key: key, value: value ,color:this.dataResult.fenlei_color_map[key]});
                    }
                    this.tableData = JSON.parse(JSON.stringify(new_tableData))
                })
        },
        updateall(){
            var dataResult_send = JSON.parse(JSON.stringify(this.dataResult))
            axios.post('/data-result/updatebyadmin',dataResult_send)
                .then(res=>{
                    if(res){
                        this.$message.success("更新成功")
                        this.dialogVisible = false
                        this.getall()
                    }
                    else this.$message.error("更新失败")
                })
                .catch(err=>{
                    this.$message.error(err)
                })
        }
    },
    mounted(){
        this.getall()
    }
}
</script>

<style scoped>
.fenlei{
    margin: 40px;
}

</style>
