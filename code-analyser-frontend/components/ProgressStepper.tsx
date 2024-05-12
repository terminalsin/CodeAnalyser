import React from 'react';

export interface Step {
  id: number;
  icon: JSX.Element;
  text: string;  // Text label for each step
}

interface StepperProps {
  currentStep: number;  // Current step in the stepper, 1-indexed
  steps: Step[];        // Array of steps to be displayed
}

const Stepper: React.FC<StepperProps> = ({ currentStep, steps }) => {
  return (
    <ol className="flex items-center justify-between w-full">
      {steps.map((step, index) => (
        <li key={step.id} className={`flex items-center justify-center flex-grow
            ${index === 0 ? 'justify-start' : ''}
            ${index === steps.length - 1 ? 'justify-end' : ''}
            ${index !== steps.length - 1 
                ? 'after:content-[""] after:w-full after:h-1 after:border-4 after:inline-block' 
                : ''} 
            ${index !== 0 
                ? 'before:content-[""] before:w-full before:h-1 before:border-4 before:inline-block' 
                : ''} 
            ${index < currentStep - 1 
                ? 'text-blue-600 dark:text-blue-500 after:border-b after:border-blue-100 dark:after:border-blue-800' 
                : 'text-gray-500 dark:text-gray-400 after:border-b after:border-gray-100 dark:after:border-gray-700'} 
            ${index < currentStep 
                ? 'text-blue-600 dark:text-blue-500 before:border-b before:border-blue-100 dark:before:border-blue-800' 
                : 'text-gray-500 dark:text-gray-400 before:border-b before:border-gray-100 dark:before:border-gray-700'} 
            ${index + 1 === currentStep 
                ? 'text-green-600 dark:text-green-500' 
                : ''}`
            }>
          <span className={`flex items-center justify-center h-10 px-4
            ${index !== steps.length - 1 
                ? "flex items-center after:content-['/'] sm:after:hidden after:mx-2" 
                : ''} 
            ${index !== 0 
                ? "flex items-center before:content-['/'] sm:before:hidden before:mx-2" 
                : ''} 
            ${index < currentStep 
                ? (index + 1 === currentStep 
                    ? 'bg-green-100 dark:bg-green-800' 
                    : 'bg-blue-100 dark:bg-blue-800') 
                : 'bg-gray-100 dark:bg-gray-700'
            } rounded-full lg:h-12 lg:w-12 shrink-0`}>
                <span className='px-1'>
                    {step.icon}
                </span>
                <span className="px-1 pl-2 text-sm font-medium">
                    {step.text}
                </span>
          </span>
          
        </li>
      ))}
    </ol>
  );
};

export default Stepper;
