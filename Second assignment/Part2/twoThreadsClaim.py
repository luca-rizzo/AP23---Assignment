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

def just_wait(n): # NOOP for n/10 seconds
    time.sleep(n * 0.1)
    
def grezzo(n): # CPU intensive
    for _ in range(2**n):
        pass

def test(iter, fun, args):
    n_threads = (1, 2, 4, 8)
    seq_iter = (16, 8, 4, 2)
    result_single_thread = bench(n_threads=n_threads[0], seq_iter=seq_iter[0], iter=iter)(fun)(*args)
    result_two_threads = bench(n_threads=n_threads[1], seq_iter=seq_iter[1], iter=iter)(fun)(*args)
    result_four_threads = bench(n_threads=n_threads[2], seq_iter=seq_iter[2], iter=iter)(fun)(*args)
    result_eight_threads = bench(n_threads=n_threads[3], seq_iter=seq_iter[3], iter=iter)(fun)(*args)
    filename = f"{fun.__name__}-{args}-{n_threads}-{seq_iter}.txt"
    with open(filename, 'w') as file:
        file.write(str(result_single_thread) + "\n")
        file.write(str(result_two_threads) + "\n")
        file.write(str(result_four_threads) + "\n")
        file.write(str(result_eight_threads) + "\n")
        
        

# Example test with 'just_wait' function
test(iter=5, fun=just_wait, args=(1,))

# Example test with 'grezzo' function
test(iter=5, fun=grezzo, args=(12,))


# This experiment demonstrates that multithreading in Python is particularly efficient for blocking operations, such as those involving IO,
# where a thread is waiting for some external event. In such cases, other threads can continue execution, making better use of system resources.
# For example, in a CRUD web application, where a significant amount of time is spent waiting for IO operations to complete,
# such as reading all the byte of a request from the socket and writing data to the database, multithreading allows the processing of multiple requests "in parallel",
# in the sense that, even if the first request is not yet terminated and is waiting for DB writing, a second request can be processed.
# However, for CPU-intensive operations where each thread competes for CPU time, multithreading may not provide significant efficiency gains.
# This is due to the Global Interpreter Lock (GIL), which allows only one thread to execute code at a time.
# So, even though multiple threads are ready, only one of them can execute at a time, and when it is its turn,
# there is a context-switching overhead before it executes. In such CPU-bound scenarios, single-threaded execution may be more performant.