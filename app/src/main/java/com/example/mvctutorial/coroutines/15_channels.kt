package com.example.mvctutorial.coroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Channel은 두 Coroutine 사이에 정보를 교환하는 전달 객체이다.
 * */
fun main() {
//    channels()
//    closingAndIterationOverChannels()
    buildingChannelProducers()
}

/**
 * Channel은 stream을 반환
 * Channel은 BlockingQueue와 유사하게 동작함
 * BlockingQueue의 put -> Channel의 send
 * BlockingQueue의 take -> Channel의 receive
 * */
private fun channels() = runBlocking {
    val channel = Channel<Int>()
    launch {
        // this might be heavy CPU-consuming computation or async logic, we'll just send five squares
        for (x in 1..5)
            channel.send(x * x)
    }
    // here we print five received integers:
    repeat(5) { println(channel.receive()) }
    println("Done!")
}

/**
 * channel은 사용하지 않으면 close할 수 있다.
 * close를 하더라도 이전 send값은 보장됨
 * */
private fun closingAndIterationOverChannels() = runBlocking {
    val channel = Channel<Int>()
    launch {
        for (x in 1..5) channel.send(x * x)
        channel.close() // we're done sending
     }

    // here we print received values using `for` loop (until the channel is closed)
    for (y in channel) println(y)
    println("Done!")
}

/**
 * coroutine으로 producer-consumer 패턴으로 표현가능
 * 생산하는 형태를 쉽게 구현하도록 제공하는 produce
 * 확장 함수 consumeEach로 소비하는쪽에서 사용
 * */
private fun buildingChannelProducers() = runBlocking {
    val squares = produce(Dispatchers.Default) {
        for (x in 1..5) send(x * x)
    }
    squares.consumeEach { println(it) }
    println("Done!")
}