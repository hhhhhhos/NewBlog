<template>
    <div @click.stop="$emit('click')">
        <div class="arrow arrow1"></div>

        <div v-show="false" class="arrow arrow3"></div>
    </div>
  </template>
  
<script>
export default {
    data() {
        return{
        }
    },
    methods:{
    },
    mounted() {
        this.$store.state.zhezhao_show = false
    }
}
</script>
  
<style scoped>
.arrow {
    width: 0;
    height: 0;
    margin-top: 5px;
    border-left: 15px solid transparent;
    border-right: 15px solid transparent;
    border-top: 15px solid;
    border-top-color: #b0b0b0d7;
    animation: flash 1.5s infinite;
}

.arrow1 {
    animation-delay: 0s;
}

.arrow2 {
    animation-delay: 0.1s;
}

.arrow3 {
    animation-delay: 0.9s;
}

@keyframes flash {
    0%, 100% {
        opacity: 1;
        transform: translateY(0);
    }
    50% {
        opacity: 0.1;
        transform: translateY(-5px);
    }
}

</style>
