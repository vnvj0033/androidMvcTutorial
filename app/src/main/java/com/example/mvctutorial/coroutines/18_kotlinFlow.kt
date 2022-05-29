package com.example.mvctutorial.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.system.measureTimeMillis

fun main() {
//    representingMultipleValues()
//    sequences()
//    asynchronousFlow()
//    flowsAreCold()
//    flowCancellation()
//    flowBuilders()
//    intermediateFlowOperators()
//    transformOperator()
//    sizeLimitingOperators()
//    terminalFlowOperators()
//    flowsAreSequential()
//    flowContext()
//    flowOnOperator()
//    buffering()
    conflation()
}

/**
 * 다수의 값은 코틀린 콜렌션을 통해 가능하다. 다음은 List를 forEach를 사용해 출력
 * */
private fun representingMultipleValues() = runBlocking {
    listOf(1, 2, 3).forEach { value -> println(value) }
}


/**
 * 자원이 소모되는 작업에는 시퀀스를 이용할 수 있다.
 * Sequence는 객체를 생성하는 시간을 yield를 만나 값을 전달하고 객체가 생성까지 지연시킨다.
 * */
private fun sequences() = runBlocking {

    fun foo(): Sequence<Int> = sequence { // sequence builder
        for (i in 1..3) {
            Thread.sleep(1000) // pretend we are computing it
            yield(i) // yield next value
        }
    }

    foo().forEach { println(it) }
}

/**
 * Flow는 flow {}, 빌더를 사용해 생성하며 빌더중 코드는 언제든 중단이 가능함
 * 값은 emit를 사용해 배출하며 collect를 통해 방출
 * */
private fun asynchronousFlow() = runBlocking {

    fun foo(): Flow<Int> = flow { // flow builder
        for (i in 1..3) {
            delay(100) // pretend we are doing something useful here
            emit(i) // emit next value
        }
    }

    println("main start!")
    launch {
        for (k in 1..3) {
            println("I'm not blocked $k")
            delay(100)
        }
    }

    foo().collect { println(it) }
    println("main end!")
}

/**
 * Flow는 sequence 처럼 cold stream으로 collect가 실행 되기 전까지 대기한다.
 * */
private fun flowsAreCold() = runBlocking {

    fun foo(): Flow<Int> = flow {
        println("Flow started")
        for (i in 1..3) {
            delay(100)
            emit(i)
        }
    }


    println("Calling foo...")
    val flow = foo()

    println("Calling collect...")
    flow.collect { println(it) }

    println("Calling collect again...")
    flow.collect { println(it) }
}

/**
 * flow 자체에는 cancel 함수를 지원하지 않아
 * 아래와 같이 타이머로 종료시키거나 launch로 감싸서 취소
 * */
private fun flowCancellation() = runBlocking {
    fun flow() = flow {
        for (i in 1..3) {
            delay(100)
            emit(i)
        }
    }

    val flow1 = flow()
    val flow2 = flow()


    withTimeoutOrNull(250) { // Timeout after 250ms
        flow1.collect { println("Emitting $it") }
    }
    println("flow1 Done")


    val fooLaunch = launch { // Timeout after 250ms
        flow2.collect { println("Emitting $it") }
    }
    delay(250)
    fooLaunch.cancel()
    println("flow2 Done")
}

/**
 * flow{...}를 이용해서 flow를 만드는건 가장 기본적인 방법
 * 값이 고정되어 있을경우 flowOf
 * 다양한 Collection들을 .asFlow() extension function으로 flow로 변경
 * */
private fun flowBuilders() = runBlocking {
    println("main start!")

    val flow1 = flowOf(1, 2, 3)
    flow1.collect { println("flow1:$it") }

    println("/////////////////") // Convert an integer range to a flow
    (1..3).asFlow().collect { println("flow2:$it") }
    println("main end!")
}

/**
 * flow에서 사용되는 map이나 filter의 블럭 안에서 delay 같은 suspending function을 사용 가능
 * */
private fun intermediateFlowOperators() = runBlocking {
    (1..3).asFlow() // a flow of requests
        .map { request -> performRequest(request) }
        .collect { response -> println(response) }
}

suspend fun performRequest(request: Int): String {
    delay(1000) // imitate long-running asynchronous work
    return "response $request"
}

/**
 * transform은 map 이나 filter처럼 간단하게 값들을 변환할 수 도 있고, 복잡한 변환을 수행하도록 할수도 있음
 * */
private fun transformOperator() = runBlocking {
    (1..3).asFlow() // a flow of requests
        .transform { request ->
            emit("Making request $request")
            emit(performRequest(request))
        }
        .collect { response -> println(response) }
}

/**
 * 몇개의 값만 처리가 필요한 경우 take를 통하여 개수를 제한
 * take는 제한된 개수까지만 flow를 수행하고 그 이후에는 cancel
 * finally를 사용해 리소스를 관리할 수 있음
 * */
private fun sizeLimitingOperators() = runBlocking {
    val numbers = flow {
        try {
            emit(1)
            emit(2)
            println("This line will not execute")
            emit(3)
        } finally {
            println("Finally in numbers")
        }
    }

    numbers.take(2).collect { println(it) }
}


/**
 * flow는 다양한 collection 을 지원
 * toList 또는 toSet : flow를 MutableList나 MutableSet으로 변환
 * first: 첫번째 원소를 반환하고 나머지는 cancel 시킴
 * reduce: 첫번째 원소에 주어진 operation을 이용하여 누적시켜 최종값을 반환
 * fold: 초기값을 입력받아 주어진 operation을 이용하여 누적시켜 최종값을 반환
 * */
private fun terminalFlowOperators() = runBlocking {
    val sum = (1..5).asFlow()
        .map { it * it } // squares of numbers from 1 to 5
        .reduce { a, b -> a + b } // sum them (terminal operator)
    println(sum)
}

/**
 * 각각의 colection으로 이루어진 flow들은 순차적으로(sequential)하게 동작
 * */
private fun flowsAreSequential() = runBlocking {
    (1..5).asFlow()
        .filter {
            println("Filter $it")
            it % 2 == 0
        }.map {
            println("Map $it")
            "string $it"
        }.collect {
            println("Collect $it")
        }
}

/**
 * flow로 만들어진 collection은 이를 호출한 caller의 coroutine context에서 수행되며
 * 이를 context preservation(context 보존)이라함
 * */
private fun flowContext() = runBlocking {
    val foo = flow {
        println("[${Thread.currentThread().name}] Started foo flow")
        for (i in 1..3) {
            emit(i)
        }
    }

    foo.collect { println("[${Thread.currentThread().name}] Collected $it") }
}


/**
 * background thread에서 수행하고, 결과를 받는 작업은 main thread에서 처리할 경우
 * withContext가 아니라 flowOn 을 이용하여 context를 바꿔줄 수 있음
 * */
private fun flowOnOperator() = runBlocking {
    @Suppress("BlockingMethodInNonBlockingContext")
    val foo: Flow<Int> = flow {
        for (i in 1..3) {
            Thread.sleep(100) // pretend we are computing it in CPU-consuming way
            println("[${Thread.currentThread().name}] Emitting $i")
            emit(i) // emit next value
        }
    }.flowOn(Dispatchers.Default) // RIGHT way to change context for CPU-consuming code in flow builder

    println("main start!")
    foo.collect { println("[${Thread.currentThread().name}] Collected $it")  }
    println("main end!")
}


/**
 * emit() 하는 부분에 buffer를 만들고 순차적인 처리가 아닌 pipelining을 통해 동시에 동작하도록 하여 시간을 감소
 * */
private fun buffering() = runBlocking {
    val foo: Flow<Int> = flow {
        for (i in 1..3) {
            delay(100) // pretend we are asynchronously waiting 100 ms
            emit(i) // emit next value
        }
    }

    val time = measureTimeMillis {
        foo.buffer().collect { value ->
            delay(300) // pretend we are processing it for 300 ms
            println(value)
        }
    }
    println("Collected in $time ms")
}

/**
 * conflate operator를 사용하여 중간값은 skip하도록 구현
 * 값을 처리하는 시점에 emit되어 쌓여있는 중간 값은 모두 버리고 마지막 값만 취함
 * */
private fun conflation() = runBlocking {

    val foo: Flow<Int> = flow {
        for (i in 1..3) {
            delay(100) // pretend we are asynchronously waiting 100 ms
            emit(i) // emit next value
            println("emit $i")
        }
    }

    val time = measureTimeMillis {
        foo.conflate()
            .collect { value ->
                try {
                    delay(300) // pretend we are processing it for 300 ms
                    println("Done $value")
                } catch (ce: CancellationException) {
                    println("Cancelled $value")
                }
            }
    }
    println("Collected in $time ms")
}