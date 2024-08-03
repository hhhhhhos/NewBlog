<template>
    <div
        ref="draggable"
        :style="styleObject"
        @mousedown="startDrag"
        @mousemove="onDrag"
        @mouseup="endDrag"
        @mouseleave="endDrag"
        class="draggable"
        v-if="show"
    >
        <van-icon class="icon_close"  name="close" @click="show=false"/>
        <slot class="slot"></slot>
    </div>
</template>

<script>
import { Icon } from 'vant';

export default {
    components:{
        'van-icon':Icon
    },
    props: {
        
        X: {
            type: Number,
            default: 0
        },
        Y: {
            type: Number,
            default: 0
        }
    },
    data() {
        return {
            show:true,
            dragging: false,
            styleObject: {
                position: 'fixed',
                left: '0px',
                top: '0px',
            },
            initialX: 0,
            initialY: 0,
            offsetX: 0,
            offsetY: 0,
        };
    },
    methods: {
        startDrag(event) {
            this.dragging = true;
            this.initialX = event.clientX - this.offsetX;
            this.initialY = event.clientY - this.offsetY;
        },
        onDrag(event) {
            if (this.dragging) {
                event.preventDefault();
                this.offsetX = event.clientX - this.initialX;
                this.offsetY = event.clientY - this.initialY;
                this.styleObject.left = `${this.offsetX}px`;
                this.styleObject.top = `${this.offsetY}px`;
            }
        },
        endDrag() {
            this.dragging = false;
        },
    },
    mounted() {
        this.offsetX = this.X;
        this.offsetY = this.Y;
        this.styleObject.left = `${this.X}px`;
        this.styleObject.top = `${this.Y}px`;
    }
};
</script>

<style>
.draggable {
    cursor: grab;
}
.draggable:active {
    cursor: grabbing;
}
.icon_close{
    position: absolute;
    top: 0;
    right: 0;
    margin: 3px 12px 0 0;
    cursor: pointer;
    font-size: 12px;
    color: ghostwhite;
}
</style>
