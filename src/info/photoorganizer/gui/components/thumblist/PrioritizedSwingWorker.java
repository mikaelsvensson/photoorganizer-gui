package info.photoorganizer.gui.components.thumblist;

import java.util.TreeSet;
import java.util.concurrent.Callable;

import javax.swing.SwingWorker;

/**
 *
 * @param <T> the result type returned by this SwingWorker's doInBackground and get methods
 * @param <V> the type used for carrying out intermediate results by this SwingWorker's publish and process methods
 * @param <P> the type used for prioritizing the tasks to perform
 */
public class PrioritizedSwingWorker<T, V, P extends Comparable<P>> extends SwingWorker<T, V>
{
    private static final int WAIT = 2000;
    private TreeSet<Task> _tasks = new TreeSet<Task>();
    private static int num = 0;
    
    private class Task implements Comparable<Task>
    {
        private int _num = num++;
        
        @Override
        public int hashCode()
        {
            return _num;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Task other = (Task) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (_num != other._num)
                return false;
            return true;
        }

        private Callable<V> _task = null;
        
        public Task(Callable<V> task, P priority)
        {
            super();
            _task = task;
            _priority = priority;
        }
        
        private P _priority = null;

        @Override
        public int compareTo(Task o)
        {
            if (o != null)
            {
                int prioCompare = _priority.compareTo(o._priority);
                if (prioCompare == 0)
                {
                    return o._num - _num;
                }
                else
                {
                    return prioCompare;
                }
            }
            else
            {
                return 0;
            }
        }

        @Override
        public String toString()
        {
            return "Task " + _num;
        }

        private PrioritizedSwingWorker getOuterType()
        {
            return PrioritizedSwingWorker.this;
        }
    }
    
    public void addTask(Callable<V> task, P priority)
    {
        synchronized (_tasks)
        {
            Task prioTask = new Task(task, priority);
            _tasks.add(prioTask);
            _tasks.notifyAll();
            System.err.println(getClass().getName() + ": ADDED " + prioTask + " (" + _tasks.size() + " items queued)");
        }
    }

    @Override
    protected T doInBackground() throws Exception
    {
        System.err.println(getClass().getName() + ": START");
//        while (true)
//        {
//            synchronized (_tasks)
//            {
//                try
//                {
//                    _tasks.wait(WAIT);
//                    if (!hasTasks())
//                    {
//                        break;
//                    }
//                }
//                catch (InterruptedException e)
//                {
//                }
//            }
//            
//            Task task = null;
//            while ((task = getTask()) != null)
//            {
//                if (isCancelled())
//                {
//                    break;
//                }
//                System.err.println(getClass().getName() + ": FOUND TASK " + task);
//                publish(task._task.call());
//            }
//            if (isCancelled())
//            {
//                break;
//            }
//        }
        Task task = null;
        while ((task = getTask()) != null)
        {
            if (isCancelled())
            {
                break;
            }
            System.err.println(getClass().getName() + ": PERFORMING " + task);
            publish(task._task.call());
        }
        System.err.println(getClass().getName() + ": END");
        return null;
    }
    
    private Task getTask()
    {
        Task res = null;
        synchronized (_tasks)
        {
            if (_tasks.isEmpty())
            {
                try
                {
                    _tasks.wait(WAIT);
                }
                catch (InterruptedException e)
                {
                }
            }
            if (!_tasks.isEmpty())
            {
                res = _tasks.last();
                System.err.println(getClass().getName() + ": PICKING " + res + " (" + _tasks.size() + " items queued)");
                _tasks.remove(res);
            }
        }
        return res;
    }
    
    private boolean hasTasks()
    {
        synchronized (_tasks)
        {
            return !_tasks.isEmpty();
        }
        
    }
    
}
