package com.hbsoo.commons

/**
 * Created by zun.wei on 2021/8/20.
 *
 */
class InjectTest {


    static void main(String[] args) {

    }

    def transpose() {
        def result = [['a', 'b'], [1, 2]].transpose()
        assert result == [['a', 1], ['b', 2]]

        def result2 = [['a', 'b'], [1, 2], [3, 4]].transpose()
        assert result2 == [['a', 1, 3], ['b', 2, 4]]
    }

    def inject() {
        def list = [1, 2, 3]
        def result = list.inject(0, { s, l -> s + l })
        result = list.inject(0) { s, l -> s + l }
        assert 6 == result

        def list2 = ['like', 'groovy']
        def result2 = list2.inject('I') { s, l -> "$s,$l" }
        assert 'I like groovy' == result2
    }

    def transposeInject() {
        def a = [1, 2, 3]
        def b = [4, 5, 6]
        def m = [a, b].transpose().inject([:]) { s, l -> s + [(l[0]): l[1]] }
        assert m == [1: 4, 2: 5, 3: 6]

    }

}
