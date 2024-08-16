<template>
    <div>

        <div class="block">
            <van-cell center title="标题">
            <template #right-icon>
                <van-switch v-model="checked" size="24" />
            </template>
            </van-cell>
        </div>

    </div>
</template>

<script>
import { Cell,Switch } from 'vant';
//import { Dialog } from 'vant';
//import axios from '@/utils'
//import { Icon } from 'vant';


export default {
    components: {
        'van-cell':Cell,
        'van-switch':Switch
    },
    props: {},
    data () {
        return {
        }
    },
    methods: {
    },
    mounted(){
        this.$store.state.zhezhao_show = false
    }
}
</script>

<style scoped>
.van-cell{
    text-align: left;
    background-color: transparent;
}
.block{
    border-radius: 10px;
    margin: 20px;
    background-color: white;
}
</style>
