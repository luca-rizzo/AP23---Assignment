import threading
import time
import statistics
import functools

def bench(n_threads: int = 1, seq_iter: int = 1, iter: int = 1):
    def decorator_bench(fun):
        @functools.wraps(fun)
        def wrapper_bench(*args, **kwargs):
            iter_results = []
            for _ in range(iter):
                start_time = time.perf_counter()
                threads = _create_threads(n_threads, seq_iter, fun, *args, **kwargs)
                _execute_thread(threads)
                end_time = time.perf_counter()
                iter_run_time = end_time - start_time
                iter_results.append(iter_run_time)
            return _create_output(fun.__name__, n_threads, seq_iter, iter, iter_results, *args)
        return wrapper_bench
    return decorator_bench


def _create_threads(n_threads, seq_iter, fun_to_execute, *args, **kwargs):
    threads = []
    for _ in range(n_threads):
        t = threading.Thread(target=_thread_fun, 
                             args=(seq_iter, fun_to_execute) + args, kwargs=kwargs)
        threads.append(t)
    return threads

def _execute_thread(threads):
    for t in threads:
        t.start()
    for t in threads:
        t.join()
                
def _create_output(fun_name, n_threads, seq_iter, iter, result, *args):
    return {
        "fun": fun_name,
        "args": args,
        "n_threads": n_threads,
        "seq_iter": seq_iter,
        "iter": iter,
        "mean": statistics.mean(result),
        "variance": statistics.variance(result)
    }

def _thread_fun(seq_iter: int, fun, *args, **kwargs):
    for _ in range(seq_iter):
        fun(*args, **kwargs)

