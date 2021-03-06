(ns codeexpress.instructions.string
  (:use [codeexpress.pushstate]
        [codeexpress.globals]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; instructions for strings

(define-registered 
  string_concat
  {:in {:string 2}
   :out {:string 1}}
  (fn [state]
    (if (not (empty? (rest (:string state))))
      (if (>= max-string-length (+ (count (stack-ref :string 1 state))
                                   (count (stack-ref :string 0 state))))
        (push-item (str (stack-ref :string 1 state)
                        (stack-ref :string 0 state))
                   :string
                   (pop-item :string (pop-item :string state)))
        state)
      state)))

(define-registered 
  string_take
  {:in {:string 1 :integer 1}
   :out {:string 1}}
  (fn [state]
    (if (and (not (empty? (:string state)))
             (not (empty? (:integer state))))
      (push-item (apply str (take (stack-ref :integer 0 state)
                                  (stack-ref :string 0 state)))
                 :string
                 (pop-item :string (pop-item :integer state)))
      state)))

(define-registered 
  string_length
  {:in {:string 1}
   :out {:integer 1}}
  (fn [state]
    (if (not (empty? (:string state)))
      (push-item (count (stack-ref :string 0 state))
                 :integer
                 (pop-item :string state))
      state)))

(define-registered
  string_atoi
  {:in {:string 1}
   :out {:integer 1}}
  (fn [state]
    (if (not (empty? (:string state)))
      (try (pop-item :string
                     (push-item (Integer/parseInt (top-item :string state))
                                :integer state))
           (catch Exception e state))
      state)))

(define-registered
  string_reverse
  {:in {:string 1}
   :out {:string 1}}
  (fn [state]
    (if (empty? (:string state))
      state
      (let [top-string (top-item :string state)]
        (push-item (apply str (reverse top-string))
                   :string
                   (pop-item :string state))))))

(define-registered
  string_parse_to_chars
  {:in {:string 1}}
   ;:out {:string 1}};; this is a variable number of outputs
  (fn [state]
    (if (empty? (:string state))
      state
      (loop [char-list (reverse (top-item :string state))
             loop-state (pop-item :string state)]
        (if (empty? char-list)
          loop-state
          (recur (rest char-list)
                 (push-item (str (first char-list)) :string loop-state)))))))

(define-registered 
  string_contained ;;true if top string is a substring of second string; false otherwise
  {:in {:string 2}
   :out {:boolean 1}}
  (fn [state]
    (if (empty? (rest (:string state)))
      state
      (let [sub (top-item :string state)
            full (stack-ref :string 1 state)
            result-boolean         (if (<= 0 (.indexOf full sub))
                                     true
                                     false)]
        (push-item result-boolean
                   :boolean
                   (pop-item :string (pop-item :string state)))))))
