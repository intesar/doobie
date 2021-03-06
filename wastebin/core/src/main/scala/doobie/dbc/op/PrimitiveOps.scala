package doobie
package dbc
package op

import Predef.{ any2Ensuring => _ } // FFS
import scalaz._
import scalaz.Kleisli.ask
import scalaz.effect.IO
import scalaz.effect.MonadCatchIO.ensuring
import scalaz.syntax.effect.monadCatchIO._
import Scalaz._


/** 
 * Base combinators for constructing primitive actions in `Kleisli[IO, (Log, S), _]`. 
 *
 * This trait does little beyond providing a type alias and some basic combinators to simplify the 
 * implementation of each set of operations. Note that there are no path-dependent types here; it's
 * all simple aliasing.
 */
trait PrimitiveOps[S] {

  // Our main monad
  type Action0[S,A] = Kleisli[IO, (Log, S), A]  

  // Local alias slicing off our carrier type `S`
  type Action[A] = Action0[S,A]

  /** Retrieve the log. */
  def log: Action[Log] =
    ask[IO, (Log, S)].map(_._1)

  /** 
   * Retrieve the payload and perform a primitive operation. This is the mechanism by which all 
   * JDBC functionality is provided and is usually what you want to use if you need access to the
   * low-level JDBC object (in order to implement vendor-specific functionality, for example, which
   * may require downcasting).
   * @param label log label for this `Action`
   * @param f the primitive action
   */
  def primitive[A](label: => String, f: S => A): Action[A] =
    push(label)(ask[IO, (Log, S)].map(p => f(p._2)))

  /** 
   * Push the given action down in the log stack, labeled as specified.
   * @param label log label for the new `Action`.
   * @param a the `Action` being pushed.
   */
  def push[A](label: => String)(a: Action[A]): Action[A] =
    log.flatMap(_.log(LogElement(label), a))

  /** 
   * Lift an action (and finalizer) from another world into this one, given an action that can 
   * produce the proper type of state. Actions that take a continuation are implemented in terms of
   * this method.
   * @param state an action to produce a new initial state of type `T`
   * @param action an `Action0` with carrier type `T` producing our final answer
   * @param cleanup a finalizer in `Action0` that is executed whether `action` succeeds or not.
   */
  def gosub[T,A](state: Action[T], action: Action0[T,A], cleanup: Action0[T, Unit]): Action[A] = 
    gosub0(state, action ensuring cleanup, "try/finally")

  /** 
   * Equivalent to `gosub`, but without a finalizer. 
   * @param state an action to produce a new initial state of type `T`
   * @param action an `Action0` with carrier type `T` producing our final answer
   */
  def gosub0[T,A](state: Action[T], action: Action0[T,A], label: String = "gosub"): Action[A] =
    log tuple state >>= (p => push(label)(action.run(p).liftIO[Action]))

}
